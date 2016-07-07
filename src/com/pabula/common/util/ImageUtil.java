package com.pabula.common.util;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author wangjie 2010-10-09
 * ͼƬ��������
 * 
 */
public class ImageUtil {
	static Logger log = Logger.getLogger(ImageUtil.class);

	public static final String[] suffixs = new String[] { "png", "jpeg", "jpg",
			"bmp", "gif" };

	private int width;

	private int height;

	private int scaleWidth;

	private double support = (double) 3.0;

	private double PI = (double) 3.14159265358978;

	private double[] contrib;

	private double[] normContrib;

	private double[] tmpContrib;

	private int startContrib, stopContrib;

	private int nDots;

	private int nHalfDots;
	
	
	/**   
	 * @author wangjie
     * ָ���߶� ��ȷ���ͼƬ
     * @param inputFile  �����ļ�
     * @param outputPicName ����ļ�
     * @param ratio ��������
     */	
    public void zoomPictureByAssign(String inputFile, String outFile,int width,int height) {
    	if(width<1||height<1){
    		log.error("��Ȼ��߸߶�Ӧ����0");
    		return;
    	}
    	File in = new File(inputFile);
    	File out = new File(outFile);
    	String picType = getSuffix(in,out);
    	if("".equals(picType)){
    		return;
    	}
    	try {
			saveImageAsJpg(in,out,picType,width,height);
		} catch (Exception e) {
			e.printStackTrace();
		}
  	}
	
	/**
	 * @author wangjie
     * ָ���߶� ��ȷ���ͼƬ
     * @param inputFile  �����ļ�
     * @param outputPicName ����ļ�
     * @param width ���
     * @param height �߶�
     */	
    public void zoomPictureByAssign(File inputFile, File outFile,int width,int height) {
    	if(width<1||height<1){
    		log.error("��Ȼ��߸߶�Ӧ����0");
    		return;
    	}
    	String picType = getSuffix(inputFile,outFile);
    	if("".equals(picType)){
    		return;
    	}
    	try {
			saveImageAsJpg(inputFile,outFile,picType,width,height);
		} catch (Exception e) {
			e.printStackTrace();
		}
  	}
	
	/**   
	 * @author wangjie
     * ���ݸ߶� ԭ���� ����ͼƬ
     * @param inputFile  �����ļ�ȫ����·�������ƣ�
     * @param outputPicName ����ļ�ȫ����·�������ƣ�
     * @param ratio ��������
     */	
    public void zoomPictureByHeight(String inputFile, String outFile,double height) {
    	if(height<1){
    		log.error("�߶�Ӧ����0");
    		return;
    	}
    	File in = new File(inputFile);
    	File out = new File(outFile);
    	String picType = getSuffix(inputFile,outFile);
    	if("".equals(picType)){
    		return;
    	}
  	 	BufferedImage Bi = null;
		try {
			Bi = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
  	    int _height =  Bi.getHeight();   
  	    double ratio = height/_height;
  	  try {
		saveImageAsJpg(in,out,picType,ratio);
	} catch (Exception e) {
		e.printStackTrace();
	}
  	}
	
	/**   
	 * @author wangjie
     * ���ݸ߶� ԭ���� ����ͼƬ
     * @param inputFile  �����ļ�
     * @param outputPicName ����ļ�
     * @param ratio ��������
     */	
    public  void zoomPictureByHeight(File inputFile, File outFile,double height) {
    	if(height<1){
    		log.error("�߶�Ӧ����0");
    		return;
    	}
    	String picType = getSuffix(inputFile,outFile);
    	if("".equals(picType)){
    		return;
    	}
  	 	BufferedImage Bi = null;
		try {
			Bi = ImageIO.read(inputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
  	    int _height =  Bi.getHeight();   
  	    double ratio = height/_height;
  	  try {
		saveImageAsJpg(inputFile,outFile,picType,ratio);
	} catch (Exception e) {
		e.printStackTrace();
	}
  	}
	
	/**   
	 * @author wangjie
     * ���ݿ�� ԭ���� ����ͼƬ
     * @param inputFile  �����ļ�ȫ����·�������ƣ�
     * @param outputPicName ����ļ�ȫ����·�������ƣ�
     * @param ratio ��������
     */	
    public void zoomPictureByWidth(String inputFile, String outFile,double width) {
    	if(width<1){
    		log.error("���Ӧ����0");
    		return;
    	}
    	File in = new File(inputFile);
    	File out = new File(outFile);
    	String picType = getSuffix(inputFile,outFile);
    	if("".equals(picType)){
    		return;
    	}
  	 	BufferedImage Bi = null;
		try {
			Bi = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
  	    int _width =  Bi.getWidth();   
  	    double ratio = width/_width;
  	  try {
		saveImageAsJpg(in,out,picType,ratio);
	} catch (Exception e) {
		e.printStackTrace();
	}
  	}
	
	/**   
	 * @author wangjie
     * ���ݿ�� ԭ���� ����ͼƬ
     * @param inputFile  �����ļ�
     * @param outputPicName ����ļ�
     * @param ratio ��������
     */	
    public  void zoomPictureByWidth(File inputFile, File outFile,double width) {
    	if(width<1){
    		log.error("���Ӧ����0");
    		return;
    	}
    	String picType = getSuffix(inputFile,outFile);
    	if("".equals(picType)){
    		return;
    	}
  	 	BufferedImage Bi = null;
		try {
			Bi = ImageIO.read(inputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
  	    int _width =  Bi.getWidth();   
  	    double ratio = width/_width;
  	  try {
		saveImageAsJpg(inputFile,outFile,picType,ratio);
	} catch (Exception e) {
		e.printStackTrace();
	}
  	}
	
	
	/**   
	 * @author wangjie
     * ���ݱ�������ͼƬ
     * @param inputFile  �����ļ�ȫ����·�������ƣ�
     * @param outputPicName ����ļ�ȫ����·�������ƣ�
     * @param ratio ��������
     */	
    public void zoomPictureByRatio(String inputFile, String outFile,double ratio) {
    	if(ratio<=0){
    		log.error("��������Ӧ����0");
    		return;
    	}
    	File in = new File(inputFile);
    	File out = new File(outFile);
    	String picType = getSuffix(in,out);
    	if("".equals(picType)){
    		return;
    	}
    	try {
			saveImageAsJpg(in,out,picType,ratio);
		} catch (Exception e) {
			e.printStackTrace();
		}
  	}
	
	/**   
	 * @author wangjie
     * ���ݱ�������ͼƬ
     * @param inputFile  �����ļ�
     * @param outputPicName ����ļ�
     * @param ratio ��������
     */	
    public void zoomPictureByRatio(File inputFile, File outputPicName,double ratio) {
    	if(ratio<=0){
    		log.error("��������Ӧ����0");
    		return;
    	}
    	String picType = getSuffix(inputFile,outputPicName);
    	if("".equals(picType)){
    		return;
    	}
    	try {
			saveImageAsJpg(inputFile,outputPicName,picType,ratio);
		} catch (Exception e) {
			e.printStackTrace();
		}
  	}
	
    //��ȡ�ļ���׺ ͬʱ���кϷ��Լ��
    private  String getSuffix(File inputFile,File outFile){
    	return getSuffix (inputFile.getPath(),outFile.getPath());
    }
    
    //��ȡ�ļ���׺ ͬʱ���кϷ��Լ��
    private  String getSuffix(String inputFile,String outFile){
    	File in = new File(inputFile);
    	File out = new File(outFile);
    	File outParent = new File(out.getParent());
    	String inName = in.getName().toLowerCase();
    	String outName = out.getName().toLowerCase();
    	String inSuffix = "";
    	String outSuffix = "";
        
    	if(!in.isFile()){
    		log.error("ͼƬ��������:�����ļ�������!!!");
    		return "";
    	}
    	if(!outParent.isDirectory()){
    		log.error("ͼƬ��������:����ļ�·��������!!!");
    		return "";
    	}
    	if(!inName.contains(".")){
    		log.error("ͼƬ��������:�����ļ����Ϸ�!!!");
    		return "";
    	}
    	if(!outName.contains(".")){ 
    		log.error("ͼƬ��������:����ļ������Ϸ�!!!");
    		return "";
    	}
    	inSuffix = inName.substring(inName.lastIndexOf(".")+1);
    	outSuffix = outName.substring(outName.lastIndexOf(".")+1);
    	if(!inSuffix.equals(outSuffix)){
    	//	log.error("ͼƬ��������:����ļ������Ϸ�!!!");
    	//	return "";
    	} 
    	//��Ϊ�ϴ���ʱ���Ѿ����ļ����ͽ����˼�� �˴�ʡ���ļ����ͼ��
        return inSuffix;
    }
    
	// fromFileStrԭͼƬ��ַ,saveToFileStr��������ͼ��ַ,formatWideth����ͼƬ���,formatHeight�߶�
    private void saveImageAsJpg(File fromFile,File saveFile, String picType,
			double ratio) throws Exception {
		BufferedImage srcImage;
		srcImage = ImageIO.read(fromFile); // construct image
		int imageWideth = srcImage.getWidth(null);
		int imageHeight = srcImage.getHeight(null);
		int changeToWideth = 0;
		int changeToHeight = 0;


		int  formatWideth = (int) (imageWideth*ratio);
		int  formatHeight = (int) (imageHeight*ratio);

		if (imageWideth > 0 && imageHeight > 0) {
			// flag=true;
			if (imageWideth / imageHeight >= formatWideth / formatHeight) {
				if (imageWideth > formatWideth) {
					changeToWideth = formatWideth;
					changeToHeight = (imageHeight * formatWideth) / imageWideth;
				} else {
					changeToWideth = imageWideth;
					changeToHeight = imageHeight;
				}
			} else {
				if (imageHeight > formatHeight) {
					changeToHeight = formatHeight;
					changeToWideth = (imageWideth * formatHeight) / imageHeight;
				} else {
					changeToWideth = imageWideth;
					changeToHeight = imageHeight;
				}
			}
		}

		srcImage = imageZoomOut(srcImage, changeToWideth, changeToHeight);
		ImageIO.write(srcImage, "JPEG", saveFile);
	}

	 // fromFileStrԭͼƬ��ַ,saveToFileStr��������ͼ��ַ,formatWideth����ͼƬ���,formatHeight�߶�
	private void saveImageAsJpg(File fromFile,File saveFile,String picType,
            int formatWideth, int formatHeight) throws Exception {
        BufferedImage srcImage;
        srcImage = ImageIO.read(fromFile); // construct image
        int imageWideth = srcImage.getWidth(null);
        int imageHeight = srcImage.getHeight(null);
        int changeToWideth = 0;
        int changeToHeight = 0;
        if (imageWideth > 0 && imageHeight > 0) {
            // flag=true;
            if (imageWideth / imageHeight >= formatWideth / formatHeight) {
                if (imageWideth > formatWideth) {
                    changeToWideth = formatWideth;
                    changeToHeight = (imageHeight * formatWideth) / imageWideth;
                } else {
                    changeToWideth = imageWideth;
                    changeToHeight = imageHeight;
                }
            } else {
                if (imageHeight > formatHeight) {
                    changeToHeight = formatHeight;
                    changeToWideth = (imageWideth * formatHeight) / imageHeight;
                } else {
                    changeToWideth = imageWideth;
                    changeToHeight = imageHeight;
                }
            }
        }
        srcImage = imageZoomOut(srcImage, changeToWideth, changeToHeight);
        ImageIO.write(srcImage, "JPEG", saveFile);
    }

	private BufferedImage imageZoomOut(BufferedImage srcBufferImage, int w, int h) {
		width = srcBufferImage.getWidth();
		height = srcBufferImage.getHeight();
		scaleWidth = w;

		if (DetermineResultSize(w, h) == 1) {
			return srcBufferImage;
		}
		CalContrib();
		BufferedImage pbOut = HorizontalFiltering(srcBufferImage, w);
		BufferedImage pbFinalOut = VerticalFiltering(pbOut, h);
		return pbFinalOut;
	}

	/** */
	/**
	 * ����ͼ��ߴ�
	 */
	private int DetermineResultSize(int w, int h) {
		double scaleH, scaleV;
		scaleH = (double) w / (double) width;
		scaleV = (double) h / (double) height;
		// ��Ҫ�ж�һ��scaleH��scaleV�������Ŵ����
		if (scaleH >= 1.0 && scaleV >= 1.0) {
			return 1;
		}
		return 0;

	} // end of DetermineResultSize()

	private double Lanczos(int i, int inWidth, int outWidth, double Support) {
		double x;

		x = (double) i * (double) outWidth / (double) inWidth;

		return Math.sin(x * PI) / (x * PI) * Math.sin(x * PI / Support)
				/ (x * PI / Support);

	}

	private void CalContrib() {
		nHalfDots = (int) ((double) width * support / (double) scaleWidth);
		nDots = nHalfDots * 2 + 1;
		try {
			contrib = new double[nDots];
			normContrib = new double[nDots];
			tmpContrib = new double[nDots];
		} catch (Exception e) {
			System.out.println("init   contrib,normContrib,tmpContrib" + e);
		}

		int center = nHalfDots;
		contrib[center] = 1.0;

		double weight = 0.0;
		int i = 0;
		for (i = 1; i <= center; i++) {
			contrib[center + i] = Lanczos(i, width, scaleWidth, support);
			weight += contrib[center + i];
		}

		for (i = center - 1; i >= 0; i--) {
			contrib[i] = contrib[center * 2 - i];
		}

		weight = weight * 2 + 1.0;

		for (i = 0; i <= center; i++) {
			normContrib[i] = contrib[i] / weight;
		}

		for (i = center + 1; i < nDots; i++) {
			normContrib[i] = normContrib[center * 2 - i];
		}
	} // end of CalContrib()

	// �����Ե
	private void CalTempContrib(int start, int stop) {
		double weight = 0;

		int i = 0;
		for (i = start; i <= stop; i++) {
			weight += contrib[i];
		}

		for (i = start; i <= stop; i++) {
			tmpContrib[i] = contrib[i] / weight;
		}

	} // end of CalTempContrib()

	private int GetRedValue(int rgbValue) {
		int temp = rgbValue & 0x00ff0000;
		return temp >> 16;
	}

	private int GetGreenValue(int rgbValue) {
		int temp = rgbValue & 0x0000ff00;
		return temp >> 8;
	}

	private int GetBlueValue(int rgbValue) {
		return rgbValue & 0x000000ff;
	}

	private int ComRGB(int redValue, int greenValue, int blueValue) {

		return (redValue << 16) + (greenValue << 8) + blueValue;
	}

	// ��ˮƽ�˲�
	private int HorizontalFilter(BufferedImage bufImg, int startX, int stopX,
			int start, int stop, int y, double[] pContrib) {
		double valueRed = 0.0;
		double valueGreen = 0.0;
		double valueBlue = 0.0;
		int valueRGB = 0;
		int i, j;

		for (i = startX, j = start; i <= stopX; i++, j++) {
			valueRGB = bufImg.getRGB(i, y);

			valueRed += GetRedValue(valueRGB) * pContrib[j];
			valueGreen += GetGreenValue(valueRGB) * pContrib[j];
			valueBlue += GetBlueValue(valueRGB) * pContrib[j];
		}

		valueRGB = ComRGB(Clip((int) valueRed), Clip((int) valueGreen),
				Clip((int) valueBlue));
		return valueRGB;

	} // end of HorizontalFilter()

	// ͼƬˮƽ�˲�
	private BufferedImage HorizontalFiltering(BufferedImage bufImage, int iOutW) {
		int dwInW = bufImage.getWidth();
		int dwInH = bufImage.getHeight();
		int value = 0;
		BufferedImage pbOut = new BufferedImage(iOutW, dwInH, bufImage
				.getType());

		for (int x = 0; x < iOutW; x++) {

			int startX;
			int start;
			int X = (int) (((double) x) * ((double) dwInW) / ((double) iOutW) + 0.5);
			int y = 0;

			startX = X - nHalfDots;
			if (startX < 0) {
				startX = 0;
				start = nHalfDots - X;
			} else {
				start = 0;
			}

			int stop;
			int stopX = X + nHalfDots;
			if (stopX > (dwInW - 1)) {
				stopX = dwInW - 1;
				stop = nHalfDots + (dwInW - 1 - X);
			} else {
				stop = nHalfDots * 2;
			}

			if (start > 0 || stop < nDots - 1) {
				CalTempContrib(start, stop);
				for (y = 0; y < dwInH; y++) {
					value = HorizontalFilter(bufImage, startX, stopX, start,
							stop, y, tmpContrib);
					pbOut.setRGB(x, y, value);
				}
			} else {
				for (y = 0; y < dwInH; y++) {
					value = HorizontalFilter(bufImage, startX, stopX, start,
							stop, y, normContrib);
					pbOut.setRGB(x, y, value);
				}
			}
		}

		return pbOut;

	} // end of HorizontalFiltering()

	private int VerticalFilter(BufferedImage pbInImage, int startY, int stopY,
			int start, int stop, int x, double[] pContrib) {
		double valueRed = 0.0;
		double valueGreen = 0.0;
		double valueBlue = 0.0;
		int valueRGB = 0;
		int i, j;

		for (i = startY, j = start; i <= stopY; i++, j++) {
			valueRGB = pbInImage.getRGB(x, i);

			valueRed += GetRedValue(valueRGB) * pContrib[j];
			valueGreen += GetGreenValue(valueRGB) * pContrib[j];
			valueBlue += GetBlueValue(valueRGB) * pContrib[j];
			// System.out.println(valueRed+"->"+Clip((int)valueRed)+"<-");
			//   
			// System.out.println(valueGreen+"->"+Clip((int)valueGreen)+"<-");
			// System.out.println(valueBlue+"->"+Clip((int)valueBlue)+"<-"+"-->");
		}

		valueRGB = ComRGB(Clip((int) valueRed), Clip((int) valueGreen),
				Clip((int) valueBlue));
		// System.out.println(valueRGB);
		return valueRGB;

	} // end of VerticalFilter()

	private BufferedImage VerticalFiltering(BufferedImage pbImage, int iOutH) {
		int iW = pbImage.getWidth();
		int iH = pbImage.getHeight();
		int value = 0;
		BufferedImage pbOut = new BufferedImage(iW, iOutH, pbImage.getType());

		for (int y = 0; y < iOutH; y++) {

			int startY;
			int start;
			int Y = (int) (((double) y) * ((double) iH) / ((double) iOutH) + 0.5);

			startY = Y - nHalfDots;
			if (startY < 0) {
				startY = 0;
				start = nHalfDots - Y;
			} else {
				start = 0;
			}

			int stop;
			int stopY = Y + nHalfDots;
			if (stopY > (int) (iH - 1)) {
				stopY = iH - 1;
				stop = nHalfDots + (iH - 1 - Y);
			} else {
				stop = nHalfDots * 2;
			}

			if (start > 0 || stop < nDots - 1) {
				CalTempContrib(start, stop);
				for (int x = 0; x < iW; x++) {
					value = VerticalFilter(pbImage, startY, stopY, start, stop,
							x, tmpContrib);
					pbOut.setRGB(x, y, value);
				}
			} else {
				for (int x = 0; x < iW; x++) {
					value = VerticalFilter(pbImage, startY, stopY, start, stop,
							x, normContrib);
					pbOut.setRGB(x, y, value);
				}
			}

		}

		return pbOut;

	} // end of VerticalFiltering()

	int Clip(int x) {
		if (x < 0)
			return 0;
		if (x > 255)
			return 255;
		return x;
	}

}