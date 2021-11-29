package Backend;

import GUI_V1.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class main {
    public static void main(String[] args){
        Login_Page L = new Login_Page();
        L.setVisible(true);
        Timer t = new Timer(4000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exited");
            }
        });
        t.start();
    }
}
