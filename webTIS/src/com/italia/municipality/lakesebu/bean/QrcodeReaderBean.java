package com.italia.municipality.lakesebu.bean;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;
import org.primefaces.event.CaptureEvent;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class QrcodeReaderBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 545464545757571L;
	@Setter
	@Getter
	private String qrcodeMsg;
	
	public void findQRCode() {
		System.out.println("now looking the qrcode......");
		final String jsonData = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("qrcode");
		
		System.out.println("jsondata: " + jsonData);
		
	}
	
	public void oncapture(CaptureEvent captureEvent) {
		System.out.println("=======================================================SCANNING==================================================================");
        try {
            if (captureEvent != null) {
                Result result = null;
                BufferedImage image = null;

                InputStream in = new ByteArrayInputStream(captureEvent.getData());

                image = ImageIO.read(in);

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                result = new MultiFormatReader().decode(bitmap);
                System.out.println("Scanning the qrcode");
                if (result != null) {
                    //setResultText(result.getText());
                	System.out.println(result.getText());
                	setQrcodeMsg(result.getText());
                }
            }
        } catch (NotFoundException ex) {
            // fall thru, it means there is no QR code in image
        	//setResultText("Image is not readable...");
        	System.out.println("==============WARNING============ERROR=========");
        } catch (ReaderException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	
}
