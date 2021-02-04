package org.cuiyang.assistant.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class AdbClient {
    private static String adbPath;
    private AndroidDebugBridge bridge;

    public static AdbClient getInstance() {
        return AdbClient.AdbClientHolder.instance;
    }

    public static void setAdbPath(String adbPath) {
        AdbClient.adbPath = adbPath;
    }

    private void init() {
        AndroidDebugBridge.init(true);

        this.bridge = AndroidDebugBridge.getBridge();
        if (this.bridge == null) {
            if (StringUtils.isEmpty(adbPath)) {
                bridge = AndroidDebugBridge.createBridge();
            } else {
                bridge = AndroidDebugBridge.createBridge(adbPath, false);
            }
        }

        long timeout = System.currentTimeMillis() + 60000L;

        while(!this.bridge.hasInitialDeviceList() && System.currentTimeMillis() < timeout) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException var5) {
                throw new RuntimeException(var5);
            }
        }
    }

    public List<IDevice> getDevices() {
        return Arrays.asList(this.bridge.getDevices());
    }

    public void shutdown() {
        AndroidDebugBridge.disconnectBridge();
        AndroidDebugBridge.terminate();
    }

    private static class AdbClientHolder {
        static final AdbClient instance = init();

        static AdbClient init() {
            AdbClient instance = new AdbClient();
            instance.init();
            return instance;
        }
    }
}
