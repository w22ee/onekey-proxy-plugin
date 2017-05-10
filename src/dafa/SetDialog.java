package dafa;

import javax.swing.*;
import java.awt.event.*;

public class SetDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton installButton;
    private JButton connectButton;
    private JButton closeButton;
    private JLabel statusLabel;
    private JLabel installStatusLabel;
    private ShellUtils shellUtils;
    private boolean isInstall = false;

    public SetDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        shellUtils = new ShellUtils(new ShellUtils.AdbCallback() {
            @Override
            public void OnSuccess() {

            }

            @Override
            public void OnFail() {
                statusLabel.setText("init fail");
            }

            @Override
            public void OnFinish() {
                statusLabel.setText("init succes ip :"+shellUtils.getPcIp());
                checkIntall();
            }

            @Override
            public void OnRunning(String s) {

            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        installButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                installButton.setText("install ing ");
                shellUtils.installApp(new ShellUtils.AdbCallback() {
                    @Override
                    public void OnSuccess() {
                        installStatusLabel.setText("已安装apk");
                    }

                    @Override
                    public void OnFail() {

                    }

                    @Override
                    public void OnFinish() {
                        installButton.setText("install ");
                    }

                    @Override
                    public void OnRunning(String s) {
//                        if (s!=null){
//                            installButton.setText(s);
//                        }
                    }
                });
            }
        });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conncetOrinstall();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shellUtils.closeVpn(new ShellUtils.AdbCallback() {
                    @Override
                    public void OnSuccess() {

                    }

                    @Override
                    public void OnFail() {

                    }

                    @Override
                    public void OnFinish() {

                    }

                    @Override
                    public void OnRunning(String s) {

                    }
                });
            }
        });

        setSize(600,300);

        setLocationRelativeTo(null);
    }

    private void checkIntall(){
        if (shellUtils!=null){
            shellUtils.isInstallApp(new ShellUtils.AdbCallback() {
                @Override
                public void OnSuccess() {
                    installStatusLabel.setText("已安装apk");
                    isInstall = true;
                }

                @Override
                public void OnFail() {
                    installStatusLabel.setText("未安装apk");
                    isInstall = false;
                }

                @Override
                public void OnFinish() {

                }

                @Override
                public void OnRunning(String s) {

                }
            });
        }
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        SetDialog dialog = new SetDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void conncetOrinstall(){
            connectButton.setText("获取 ip 中");
            System.out.println("检测到一键代理app,请在app上同意vpn使用权限");
            connectButton.setText("connect ing");
            shellUtils.openActivity( new ShellUtils.AdbCallback() {
                @Override
                public void OnSuccess() {

                }

                @Override
                public void OnFail() {

                }

                @Override
                public void OnFinish() {
                    connectButton.setText("connect");
                }

                @Override
                public void OnRunning(String s) {

                }
            });
    }
}
