package dnf.module.auction.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
public class ImgController {
    @GetMapping(value="/image/view/{filename}", produces= MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage(@PathVariable("filename") String filename) throws IOException {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String fileDir = "D:\\img\\" + filename; // 파일경로

        try{
            fis = new FileInputStream(fileDir);
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }

        int readCount = 0;
        byte[] buffer = new byte[1024];
        byte[] fileArray = null;

        try{
            while((readCount = fis.read(buffer)) != -1){
                baos.write(buffer, 0, readCount);
            }
            fileArray = baos.toByteArray();
            fis.close();
            baos.close();
        } catch(IOException e){
            throw new RuntimeException("File Error");
        }
        return fileArray;
    }
}
