package dafa;


import com.android.ddmlib.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by lixi on 16/7/6.
 */
public class ShellUtils {

    private  File apkfile;
    private IDevice iDevice;
    private IDevice[] deviceList;
    public AndroidDebugBridge adb = null;
    private String pcIp;

    public ShellUtils(AdbCallback callback) {


        AndroidDebugBridge.initIfNeeded(false);

        adb = AndroidDebugBridge.getBridge();
        if (adb == null) {
            adb = AndroidDebugBridge.createBridge();
        }

        DdmPreferences.setTimeOut(10000);

        String home = System.getProperty("user.home");
        apkfile = new File(home+"/Downloads/onekey.apk");

        new Thread(new Runnable() {
            @Override
            public void run() {
                refreshIp();
                copyApk();
                waitDeviceList(adb, callback);
            }
        }).start();
    }

    public void initDevice() {
        if (adb != null) {
            deviceList = adb.getDevices();
            if (deviceList != null && deviceList.length > 0) {
                iDevice = deviceList[0];
            } else {

            }
        }
    }

    public String getPcIp() {
        return pcIp;
    }

    public void refreshIp(){
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        pcIp = inetAddress.getHostAddress();
    }

    private void waitDeviceList(AndroidDebugBridge bridge, AdbCallback adbCallback) {
        int count = 0;
        while (bridge.hasInitialDeviceList() == false) {
            try {
                Thread.sleep(100);
                count++;
            } catch (InterruptedException e) {
            }
            if (count > 100) {
                System.out.println("Fail to Init Device list");
                adbCallback.OnFail();
                break;
            }
        }
        adbCallback.OnFinish();
    }

    public void runCommand(String command, String expectOutput, AdbCallback callback) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                GenericReceiver genericReceiver = new GenericReceiver();
                try {
                    if (iDevice == null) {
                        initDevice();
                    }
                    iDevice.executeShellCommand(command, genericReceiver, 15l, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (AdbCommandRejectedException e) {
                    e.printStackTrace();
                } catch (ShellCommandUnresponsiveException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    e.printStackTrace();
                }

                if (genericReceiver != null && genericReceiver.getAdbOutputLines() != null && genericReceiver.getAdbOutputLines().size() > 0) {

                    boolean succes = false;

                    for (String s : genericReceiver.getAdbOutputLines()) {
                        if (s != null && expectOutput != null && s.contains(expectOutput)) {
                            if (callback != null) {
                                callback.OnSuccess();
                                succes = true;
                            }
                        }
                    }
                    if (callback != null) {
                        if (!succes) {
                            callback.OnFail();
                        }
                        callback.OnFinish();
                    }
                }
            }
        }).start();


    }

    public void isInstallApp(AdbCallback adbCallback) {
        try {
            runCommand("pm list packages", "com.napos.onkey", adbCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openActivity(AdbCallback adbCallback) {
        try {
            runCommand(" am start -n com.napos.onkey/me.smartproxy.ui.MainActivity --es pc_ip " + pcIp, null, adbCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyApk(){
        InputStream  inputStream = getClass().getResourceAsStream("/apk/onekey.apk");
        OutputStream  outputStream = null;

        try
        {
            if (apkfile.exists()){
            }else {
                apkfile.createNewFile();
            }

            //使用FileInputStream和FileOutputStream进行文件复制
            FileOutputStream fos=new FileOutputStream(apkfile);
            int read;
            //read=fis.read();
            byte b[]=new byte[1024];
            //读取文件，存入字节数组b，返回读取到的字符数，存入read,默认每次将b数组装满
            read=inputStream.read(b);
            while(read!=-1)
            {
                fos.write(b,0,read);
                read=inputStream.read(b);
                //read=fis.read();
            }
            inputStream.close();
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }


    public void installApp(AdbCallback adbCallback) {
        if (iDevice == null) {
            adbCallback.OnFail();
            adbCallback.OnRunning("iDevice == null");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (apkfile==null||!apkfile.exists()){
                        adbCallback.OnRunning(" apk copy err");
                    }

                    String path = apkfile.getPath();

                    adbCallback.OnRunning(" url = "+path);
                    System.out.println("url = " + path);

                    if (iDevice==null){
                        initDevice();
                    }

                    iDevice.installPackage(path, true, "");
                    adbCallback.OnSuccess();
                    adbCallback.OnFinish();
                } catch (InstallException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public void closeVpn(AdbCallback adbCallback) {
        try {
            runCommand(" am start -n com.napos.onkey/me.smartproxy.ui.MainActivity --ez close " + true, null, adbCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    interface AdbCallback {
        void OnSuccess();

        void OnFail();

        void OnFinish();

        void OnRunning(String s);
    }
}
