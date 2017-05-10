package dafa;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by lixi on 2017/3/31.
 */
public class ShowDialog extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        SetDialog setDialog = new SetDialog();
        setDialog.setVisible(true);
    }
}
