package principal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MzclaMultipleUI extends JFrame implements ActionListener {
	
	   private JButton btnOrdenar;

	    public MzclaMultipleUI() {
	        super("Mezcla Equilibrada Múltiple");
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	        setSize(300, 150);
	        setLocationRelativeTo(null);
	        btnOrdenar = new JButton("Ordenar Datos");
	        btnOrdenar.addActionListener(this);
	        add(btnOrdenar, BorderLayout.CENTER);
	    }

	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> new MzclaMultipleUI().setVisible(true));
	    }

	    public void actionPerformed(ActionEvent e) {
	        btnOrdenar.setEnabled(false);
	        new SwingWorker<Void, Void>() {
	            @Override protected Void doInBackground() {
	                try {
	                    MzclaMultiple.main(null);
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                }
	                return null;
	            }

	            @Override protected void done() {
	                btnOrdenar.setEnabled(true);
	                JOptionPane.showMessageDialog(
	                    MzclaMultipleUI.this,
	                    "¡Salida escrita en: C:\\Users\\tamay\\Desktop\\FicheroOrdenado.txt"
	                );
	            }
	        }.execute();
	    }

}
