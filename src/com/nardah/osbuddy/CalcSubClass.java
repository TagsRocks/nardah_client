package com.nardah.osbuddy;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

@SuppressWarnings("unused")
public class CalcSubClass extends JFrame {
    private static final long serialVersionUID = 1L;

    JTextField numDisplay;
    JButton zero;
    JButton one;
    JButton two;
    JButton three;
    JButton four;
    JButton five;
    JButton six;
    JButton seven;
    JButton eight;
    JButton nine;
    JButton add;
    JButton subtract;
    JButton multiply;
    JButton divide;
    JButton percent;
    JButton equals;
    JButton clear;
    String addString;
    String subString;
    String multiString;
    String divString;
    String finalString;
    double addDouble;
    double subDouble;
    double multiDouble;
    double divDouble;
    double finalDouble;
    double answer;
    String answerAsString;
    String percentAsString;
    double percentAsDouble;
    double prcntAnswerAsDouble;
    String prcntAnswerAsString;
    JButton decimalPoint;

public static double add(double num1, double num2) {

    return num1 + num2;
}

public static double subtract(double num1, double num2) {

    return num1 - num2;
}

public static double multiply(double num1, double num2) {

    return num1 * num2;
}

public static double divide(double num1, double num2) {

    return num1 / num2;
}

public CalcSubClass() {
    super("Calculator");
    setLayout(new GridBagLayout());

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
    setResizable(false);

    GridBagConstraints gbc = new GridBagConstraints();

    numDisplay = new JTextField();
    numDisplay.setPreferredSize(new Dimension(130, 30));
    numDisplay.setEditable(false);
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(numDisplay, gbc);

    zero = new JButton("0");
    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(zero, gbc);

    one = new JButton("1");
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    add(one, gbc);

    two = new JButton("2");
    gbc.gridx = 2;
    gbc.gridy = 2;
    add(two, gbc);

    three = new JButton("3");
    gbc.gridx = 3;
    gbc.gridy = 2;
    add(three, gbc);

    four = new JButton("4");
    gbc.gridx = 1;
    gbc.gridy = 3;
    add(four, gbc);

    five = new JButton("5");
    gbc.gridx = 2;
    gbc.gridy = 3;
    add(five, gbc);

    six = new JButton("6");
    gbc.gridx = 3;
    gbc.gridy = 3;
    add(six, gbc);

    seven = new JButton("7");
    gbc.gridx = 1;
    gbc.gridy = 4;
    add(seven, gbc);

    eight = new JButton("8");
    gbc.gridx = 2;
    gbc.gridy = 4;
    add(eight, gbc);

    nine = new JButton("9");
    gbc.gridx = 3;
    gbc.gridy = 4;
    add(nine, gbc);

    add = new JButton("+");
    gbc.gridx = 1;
    gbc.gridy = 1;
    add(add, gbc);

    subtract = new JButton("-");
    gbc.gridx = 2;
    gbc.gridy = 1;
    add(subtract, gbc);

    multiply = new JButton("x");
    gbc.gridx = 3;
    gbc.gridy = 1;
    add(multiply, gbc);

    divide = new JButton("÷");
    gbc.gridx = 4;
    gbc.gridy = 2;
    add(divide, gbc);

    percent = new JButton("%");
    gbc.gridx = 4;
    gbc.gridy = 1;
    add(percent, gbc);

    decimalPoint = new JButton(".");
    gbc.gridx = 3;
    gbc.gridy = 5;
    add(decimalPoint, gbc);

    equals = new JButton("=");
    gbc.gridx = 4;
    gbc.gridy = 3;
    gbc.gridheight = 3;
    gbc.fill = GridBagConstraints.VERTICAL;
    add(equals, gbc);

    clear = new JButton("AC");
    gbc.gridx = 4;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    add(clear, gbc);

    zero.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + "0");

        }

    });

    one.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + "1");

        }

    });

    two.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + "2");

        }

    });

    three.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + "3");

        }

    });

    four.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + "4");

        }

    });

    five.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + "5");

        }

    });

    six.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + "6");

        }

    });

    seven.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + "7");

        }

    });

    eight.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + "8");

        }

    });

    nine.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + "9");

        }

    });

    add.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            addString = numDisplay.getText();
            addDouble = Double.parseDouble(addString);
            numDisplay.setText("");

        }

    });

    subtract.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            subString = numDisplay.getText();
            subDouble = Double.parseDouble(subString);
            numDisplay.setText("");
            decimalPoint.setEnabled(true);

        }

    });

    multiply.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            multiString = numDisplay.getText();
            multiDouble = Double.parseDouble(multiString);
            numDisplay.setText("");
            decimalPoint.setEnabled(true);

        }

    });

    divide.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            divString = numDisplay.getText();
            divDouble = Double.parseDouble(divString);
            numDisplay.setText("");
            decimalPoint.setEnabled(true);
        }

    });

    percent.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            percentAsString = numDisplay.getText();
            percentAsDouble = Double.parseDouble(percentAsString);
            prcntAnswerAsDouble = percentAsDouble * 0.01;
            prcntAnswerAsString = Double.toString(prcntAnswerAsDouble);
            numDisplay.setText(prcntAnswerAsString);
        }

    });

    decimalPoint.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText(numDisplay.getText() + ".");
            String numDisplayContents = numDisplay.getText();

            if (numDisplayContents.contains(".")) {
                decimalPoint.setEnabled(false);
            }

        }

    });
    clear.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            numDisplay.setText("");
            addString = null;
            subString = null;
            multiString = null;
            divString = null;
            finalString = null;
            prcntAnswerAsString = null;
        }

    });

    equals.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            finalString = numDisplay.getText();
            finalDouble = Double.parseDouble(finalString);

            if (addString != null) {
                answer = add(addDouble, finalDouble);
                answerAsString = Double.toString(answer);
                numDisplay.setText(answerAsString);

            } else if (subString != null) {
                answer = subtract(subDouble, finalDouble);
                answerAsString = Double.toString(answer);
                numDisplay.setText(answerAsString);

            } else if (multiString != null) {
                answer = multiply(multiDouble, finalDouble);
                answerAsString = Double.toString(answer);
                numDisplay.setText(answerAsString);

            } else if (divString != null) {
                answer = divide(divDouble, finalDouble);
                answerAsString = Double.toString(answer);
                numDisplay.setText(answerAsString);

            } else {
                addString = null;
                subString = null;
                multiString = null;
                divString = null;
            }
        }

    });

  }

}
