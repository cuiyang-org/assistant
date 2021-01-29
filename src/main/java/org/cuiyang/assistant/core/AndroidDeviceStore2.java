package org.cuiyang.assistant.core;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.DdmPreferences;
import com.android.ddmlib.IDevice;
import com.github.cosysoft.device.DeviceStore;
import com.github.cosysoft.device.android.AndroidDevice;
import com.github.cosysoft.device.android.impl.DefaultHardwareDevice;
import com.github.cosysoft.device.android.impl.DeviceChangeListener;
import com.github.cosysoft.device.exception.AndroidDeviceException;
import com.github.cosysoft.device.exception.DeviceNotFoundException;
import com.github.cosysoft.device.exception.NestedException;
import org.apache.commons.lang3.StringUtils;
import org.cuiyang.assistant.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static org.cuiyang.assistant.constant.ConfigConstant.ADB_PATH;

public class AndroidDeviceStore2 implements DeviceStore {

    protected static final Logger logger = LoggerFactory
            .getLogger(AndroidDeviceStore2.class);

    private Map<IDevice, AndroidDevice> connectedDevices = new HashMap<>();

    private AndroidDebugBridge bridge;
    private boolean shouldKeepAdbAlive = false;

    static class DeviceStoreHolder {

        static final AndroidDeviceStore2 instance = init();

        static AndroidDeviceStore2 init() {
            AndroidDeviceStore2 instance;
            instance = new AndroidDeviceStore2();
            instance.initAndroidDevices(false);
            return instance;
        }
    }

    public static AndroidDeviceStore2 getInstance() {
        return AndroidDeviceStore2.DeviceStoreHolder.instance;
    }

    /**
     * call once
     */
    public void initAndroidDevices(boolean shouldKeepAdbAlive)
            throws AndroidDeviceException {
        //        DdmPreferences.setLogLevel(LogLevel.VERBOSE.getStringValue());
        DdmPreferences.setInitialThreadUpdate(true);
        DdmPreferences.setInitialHeapUpdate(true);
        this.initializeAdbConnection();
    }

    /**
     * Initializes the AndroidDebugBridge and registers the DefaultHardwareDeviceManager with the
     * AndroidDebugBridge device change listener.
     */
    protected void initializeAdbConnection() {
        // Get a device bridge instance. Initialize, create and restart.
        try {
            AndroidDebugBridge.init(true);
        } catch (IllegalStateException e) {
            // When we keep the adb connection alive the AndroidDebugBridge may
            // have been already
            // initialized at this point and it generates an exception. Do not
            // print it.
            if (!shouldKeepAdbAlive) {
                logger.error(
                        "The IllegalStateException is not a show "
                                + "stopper. It has been handled. This is just debug spew. Please proceed.",
                        e);
                throw new NestedException("ADB init failed", e);
            }
        }

        bridge = AndroidDebugBridge.getBridge();

        if (bridge == null) {
            String adbPath = ConfigUtils.get(ADB_PATH);
            if (StringUtils.isEmpty(adbPath)) {
                bridge = AndroidDebugBridge.createBridge();
            } else {
                bridge = AndroidDebugBridge.createBridge(adbPath, false);
            }
        }

        long timeout = System.currentTimeMillis() + 60000;
        while (!bridge.hasInitialDeviceList()
                && System.currentTimeMillis() < timeout) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // Add the existing devices to the list of devices we are tracking.
        IDevice[] devices = bridge.getDevices();
        logger.info("initialDeviceList size {}", devices.length);
        for (IDevice device : devices) {
            logger.info("devices state: {},{} ", device.getName(),
                    device.getState());
            connectedDevices.put(device, new DefaultHardwareDevice(
                    device));
        }

        AndroidDebugBridge.addDeviceChangeListener(new DeviceChangeListener(connectedDevices));
    }

    @Override
    public TreeSet<AndroidDevice> getDevices() {
        return new TreeSet<>(connectedDevices.values());
    }

    @Override
    public AndroidDevice getDeviceBySerial(String serialID) {
        for (AndroidDevice device : getDevices()) {
            if (device.getSerialNumber().equalsIgnoreCase(serialID)) {
                return device;
            }
        }
        throw new DeviceNotFoundException(String.format("Device %s not found", serialID));
    }

    /**
     * Shutdown the AndroidDebugBridge and clean up all connected devices.
     */
    @Override
    public void shutdown() {
        if (!shouldKeepAdbAlive) {
            AndroidDebugBridge.disconnectBridge();
            AndroidDebugBridge.terminate();
        }
        logger.info("stopping Device Manager");
    }

    /**
     * used with caution or don't call this method
     */
    @Override
    public void shutdownForcely() {
        AndroidDebugBridge.disconnectBridge();
        AndroidDebugBridge.terminate();
    }
}
