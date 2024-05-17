package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.entity.MerchantUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DynamicPromotionalImageBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicPromotionalImageBO.class);
    private final FirebaseBO firebaseBO;
    private final NotificationService notificationService;
    private static final String promotionalCreative = "valentine_poster";

    @Autowired
    public DynamicPromotionalImageBO(FirebaseBO firebaseBO,
                                     NotificationService notificationService) {
        this.firebaseBO = firebaseBO;
        this.notificationService = notificationService;
    }

    //if filename is populated then select a dynamic url that contains filename as a substring a upload for merchant
    public List<String> getSharableUrls(MerchantUser user, String filename) {
        List<String> imageUrls = firebaseBO.getFolderContentUrlList("offline_merchants/promotions/baseFiles/dynamic");
        List<String> staticImageUrls = firebaseBO.getFolderContentUrlList("offline_merchants/promotions/baseFiles/static");
        if (StringUtils.hasText(filename)) {
            LOGGER.info("Since file name: {} is a non-empty, uploading dynamic creative contains this filename for merchant id: {}", filename, user.getId().toString());
            Optional<String> dynamicImageUrl = imageUrls.stream().filter(url -> url.contains(filename)).findFirst();
            if (dynamicImageUrl.isPresent()) {
                LOGGER.info("Dynamic creative url: {} found for file name: {} for merchant id: {}", dynamicImageUrl.get(), filename, user.getId().toString());
                String merchantShareImageUrl = getMerchantShareImageUrl(user, dynamicImageUrl.get());
                user.getShareImages().add(merchantShareImageUrl);
                return user.getShareImages();
            }
        }
        return getSharableUrls(imageUrls, staticImageUrls, user);
    }

    public List<String> getSharableUrls(List<String> imageUrls, List<String> staticImageUrls, MerchantUser user) {
        List<String> merchantShareImageUrls = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            String merchantShareImageUrl = getMerchantShareImageUrl(user, imageUrl);
            if (StringUtils.isEmpty(merchantShareImageUrl)) {
                continue;
            }
            merchantShareImageUrls.add(merchantShareImageUrl);
        }
        merchantShareImageUrls.addAll(staticImageUrls);
        return merchantShareImageUrls;
    }

    private String getMerchantShareImageUrl(MerchantUser user, String imageUrl) {
        String[] urlSplit = imageUrl.split("/");
        String filename = urlSplit[urlSplit.length - 1];
        if (filename.split("\\.").length == 1) {
            return null;
        }
        String ext = filename.split("\\.")[1];
        String path = "offline_merchants/promotions/images/" + user.getId().toString() + "/" + filename;
        BufferedImage image = getEditedImage(user.getShopName(), user.getMobile(), imageUrl, filename);
        if (image == null) {
            return null;
        }
        byte[] babi = getByteArrayFromBufferedImage(image, ext);
        if (babi == null) {
            return null;
        }
        return firebaseBO.uploadImageByteArray(path, "image/" + ext, babi);
//        return firebaseBO.getBucketUrl() + path;
    }

    private BufferedImage getEditedImage(String shopName, String mobile, String imageUrl, String filename) {
        try {
            if (null != filename && filename.contains(promotionalCreative)) {
                BufferedImage image = ImageIO.read(new URL(imageUrl));
                Graphics g = image.getGraphics();
                g.setFont(new Font("Arial Black", Font.PLAIN, 120));
                g.setColor(Color.decode("#FFFFFF"));
                g.drawString(shopName, 120, 520);
                g.dispose();
                return image;
            }
            BufferedImage image = ImageIO.read(new URL(imageUrl));
            Graphics g = image.getGraphics();
            g.setFont(new Font("Roboto", Font.BOLD,
                    (Math.round(shopName.length() > 19 ? (100f * 19f) / ((float) shopName.length()) : 100f))));
            g.setColor(Color.decode("#64489C"));
            g.drawString(shopName.toUpperCase(), 125, 300);
//            g.setFont(g.getFont().deriveFont(Font.PLAIN, 50f));
//            g.drawString("Address", 125, 1800);
//            g.drawString("-", 320, 1800);
//            g.drawString(user.getAddress().getLine1(), 350, 1800);
//            g.drawString(user.getAddress().getLine2(), 350, 1850);
//            g.drawString(user.getAddress().getCity() + ", " + user.getAddress().getPincode(), 350, 1900);
//            g.drawString("Contact Us", 125, 1800);
//            g.drawString("-", 320, 1800);
//            g.drawString(user.getMobile(), 350, 1800);
            g.setFont(new Font("Roboto", Font.PLAIN, Math.round(60f)));
            g.drawString("Contact Us", 125, 1900);
            g.drawString("-", 440, 1900);
            g.drawString(mobile, 470, 1900);
            g.dispose();
            return image;
        } catch (IOException e) {
            LOGGER.error("Exception occurred while editing images", e);
            notificationService.sendException(String.format("Update Share Images issue for %s", shopName));
            return null;
        }
    }

    private byte[] getByteArrayFromBufferedImage(BufferedImage image, String type) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, type, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Exception occurred while getting byte array from image", e);
            notificationService.sendException("Update Share Images issue");
            return null;
        }
    }

//    public void updateShareImages() {
//        List<String> imageUrls = firebaseBO.getFolderContentUrlList("offline_merchants/promotions/baseFiles/dynamic");
//        List<String> staticImageUrls = firebaseBO.getFolderContentUrlList("offline_merchants/promotions/baseFiles/static");
//        List<MerchantUser> merchants = merchantUserRepository.findAll();
//        for (MerchantUser user : merchants) {
//            List<String> images = getSharableUrls(imageUrls, staticImageUrls, user);
//            user.setShareImages(images);
//            merchantUserRepository.save(user);
//        }
//    }

//    public static void main(String[] args) throws IOException {
//        String url = "https://paymentassets.s3.ap-south-1.amazonaws.com/logos/valentine_poster1102221444450196188123.png";
//        String[] urlSplit = url.split("/");
//        String filename = urlSplit[urlSplit.length - 1];
//        if (filename.split("\\.").length != 1) {
//            String ext = filename.split("\\.")[1];
//            String path = filename;
//            BufferedImage image = getEditedImage("Dharmik Enterprises", "9096167416", url, filename);
//            if (image != null) {
//                byte[] babi = getByteArrayFromBufferedImage(image, ext);
//                if (babi != null) {
//                    File f = new File(filename);
//                    ///karo
//                    ImageIO.write(image, ext, f);
//                }
//            }
//        }
//    }
}
