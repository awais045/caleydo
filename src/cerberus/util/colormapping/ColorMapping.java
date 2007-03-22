package cerberus.util.colormapping;

import java.awt.Color;

public class ColorMapping {

	protected int iMin = 0;
	protected int iMax = 0;
	
	protected Color color_1 = Color.RED;
	protected Color color_2 = Color.GREEN;
	protected Color resultColor;
	
	int iWidth;
	
	float[][] fArColorLookupTable;
	
	/**
	 * Constructor.
	 *
	 */
	public ColorMapping(int iMin, int iMax) {
		
		this.iMin = iMin;
		this.iMax = iMax;
		
		iWidth = iMax - iMin;
		
		fArColorLookupTable = new float[iWidth][3];
	}
	
	public void createLookupTable() {
		
		for (int iLookupIndex = 0; iLookupIndex < iWidth; iLookupIndex++)
		{
			fArColorLookupTable[iLookupIndex][0] = (color_1.getRed() + 
				((color_2.getRed() - color_1.getRed()) / ((float)iMax - (float)iMin)) * ((float)iLookupIndex - (float)iMin)) / 255.0f; 
			
			fArColorLookupTable[iLookupIndex][1] = (color_1.getGreen() + 
				((color_2.getGreen() - color_1.getGreen()) / ((float)iMax - (float)iMin)) * ((float)iLookupIndex - (float)iMin)) / 255.0f; 
	
			fArColorLookupTable[iLookupIndex][2] = (color_1.getBlue() + 
				((color_2.getBlue() - color_1.getBlue()) / ((float)iMax - (float)iMin)) * ((float)iLookupIndex - (float)iMin)) / 255.0f; 
			
//			System.out.println(fArColorLookupTable[iLookupIndex][0] + "," +
//					fArColorLookupTable[iLookupIndex][1] + "," +
//					fArColorLookupTable[iLookupIndex][2]);
		}
		
//		int iTextureId = genTexture(gl);
//		
//		gl.glEnable(GL.GL_TEXTURE_1D);
//		gl.glBindTexture(GL.GL_TEXTURE_1D, iTextureId);
//		gl.glTexImage1D(GL.GL_TEXTURE_1D, 0, GL.GL_RGB, iTextureWidth, 0, GL.GL_RGB, GL.GL_FLOAT, arTexture);

	}
	
//    private int genTexture(GL gl) {
//    	
//        final int[] tmp = new int[1];
//        gl.glGenTextures(1, tmp, 0);
//        return tmp[0];
//    }
	
	public Color colorMappingLookup(int iLookupValue) {
		
		if (iLookupValue < iMin || iLookupValue > iMax)
			return Color.BLACK;

		return new Color(fArColorLookupTable[iLookupValue][0],
				fArColorLookupTable[iLookupValue][1],
				fArColorLookupTable[iLookupValue][2]);
	}
}
