package org.takre.core.views;

import static java.awt.AWTEventMulticaster.add;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CharacterCreator extends JPanel {
    private JTextField nombreInput;
    private JButton colorBoton;
    private JComboBox<String> peinadosBox;
    private JPanel previewPanel;
    private Color colorSeleccionado = Color.BLUE;
    private String peinadoSeleccionado = "Corto";

    public CharacterCreator() {
        setLayout(new BorderLayout());

        nombreInput = new JTextField("Mi personaje");
        colorBoton = new JButton("Color");
        peinadosBox = new JComboBox<>(new String[]{"Corto", "Largo", "Rulos"});

        previewPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(colorSeleccionado);
                g.fillRect(50, 50, 40, 40);
                g.setColor(Color.BLACK);
                g.drawString(peinadoSeleccionado, 50, 45);
            }
        };

        colorBoton.addActionListener(e -> {
            Color nuevo = JColorChooser.showDialog(this, "Selecciona color", colorSeleccionado);
            if (nuevo != null) {
                colorSeleccionado = nuevo;
                previewPanel.repaint();
            }
        });

        peinadosBox.addActionListener(e -> {
            peinadoSeleccionado = (String) peinadosBox.getSelectedItem();
            previewPanel.repaint();
        });

        JButton crear = new JButton("Crear personaje");
        crear.addActionListener(e -> {
            String nombre = nombreInput.getText();
            System.out.println("Nombre: " + nombre);
            System.out.println("Color: " + colorSeleccionado);
            System.out.println("Peinado: " + peinadoSeleccionado);
            // Aquí podrías guardar esto o pasarlo al GamePanel
        });

        JPanel controles = new JPanel();
        controles.add(new JLabel("Nombre:"));
        controles.add(nombreInput);
        controles.add(colorBoton);
        controles.add(peinadosBox);
        controles.add(crear);

        add(controles, BorderLayout.NORTH);
        add(previewPanel, BorderLayout.CENTER);

        setSize(300, 300);
        setVisible(true);
    }
}

