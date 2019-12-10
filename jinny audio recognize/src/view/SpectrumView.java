package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import model.Complex;

public class SpectrumView extends javax.swing.JFrame {

	Complex[][] results = null;
	int size;
	double highscores[][];
	double recordPoints[][];


	public SpectrumView(Complex[][] results, int size, double highscores[][],
			double recordPoints[][]) {
		this.results = results;
		this.size = size;
		this.highscores = highscores;
		this.recordPoints = recordPoints;
		this.setLayout(new FlowLayout());
		this.setSize(400, 800);
	}

//выводит график, ахуенный
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		int blockSizeX = 2;
		int blockSizeY = 3;
		size = 500;
		for (int i = 0; i < results.length; i++) {
			int freq = 1;
			for (int line = 1; line < size; line++) {
		
				double magnitude = Math.log(results[i][freq].abs() + 1);
		
				g2d.setColor(new Color((int) magnitude * 10,0 ,
						(int) magnitude * 10));


			
				g2d.fillRect(i * blockSizeX, (size - line) * blockSizeY,
						blockSizeX, blockSizeY);

				if (false && (Math.log10(line) * Math
						.log10(line)) > 1) {
					freq += (int) (Math.log10(line) * Math.log10(line));
				} else {
					freq++;
				}
			}
		}
	}

}
