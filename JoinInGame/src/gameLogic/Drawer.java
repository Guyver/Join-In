package gameLogic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Drawer extends Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BufferedImage img;
	private String imgRoute;
	private JFrame frame; 
	
	
	public void paint(Graphics g){
		g.drawImage(img, 0, 0, null);	
	
	}
	
	
	public Drawer (String imgRoute){
		frame = new JFrame("Join-In Game");
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		
		this.imgRoute=imgRoute;
		try{
			img = ImageIO.read(new File(imgRoute));
			frame.add(this);
			frame.pack();
			frame.setVisible(true);
			frame.setLocation(100, 100);
			frame.setSize(1024, 960);
		}catch(IOException e){
			System.out.println("Invalid image route");
		}
		
		
		
	}
	
	
	public Dimension getPreferedSize(){
		if(img ==null){
			return new Dimension(1000,1000);
		}else{
			return new Dimension(img.getWidth(null),img.getHeight(null));
		}
	}
	
	
	public void setImg(BufferedImage img) {
		this.img = img;
	}
	public BufferedImage getImg() {
		return img;
	}
	public void setImgRoute(String imgRoute) {
		this.imgRoute = imgRoute;
	}
	public String getImgRoute() {
		return imgRoute;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public JFrame getFrame() {
		return frame;
	}
	
	
	public void loadNewImage(String newImgRoute){
		this.imgRoute=newImgRoute;
		try{
			img = ImageIO.read(new File(imgRoute));
		}catch (IOException e){
		
		}
		frame.repaint();
		
	}
	

}