package kr.co.tjeit.bluetoothpractice.data;

import java.io.Serializable;

/**
 * Created by the on 2017-09-13.
 */

public class BTDevice implements Serializable {
    private String deviceName;
    private String deviceAddress;   // 기기 MAC 주소

    public BTDevice() {
    }

    public BTDevice(String deviceName, String deviceAddress) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
