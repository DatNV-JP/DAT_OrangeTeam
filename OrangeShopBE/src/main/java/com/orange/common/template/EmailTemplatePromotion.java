package com.orange.common.template;

import com.orange.utils.XDate;
import com.orange.utils.XNumber;
import com.orange.domain.model.*;

import java.util.List;

public class EmailTemplatePromotion {
    public static String generateHtmlPromotionCreatedEmail(Promotion promotion, User user) {
        String[] contents = new String[]{
                "Chúng tôi xin gửi lời cảm ơn đến quý khách đã tin tưởng và mua sắm tại " + EmailTemplate.SHOP_NAME + ". Chúng tôi rất trân trọng sự tin tưởng và ủng hộ của Quý khách.",
                "Để đáp lại sự tin tưởng của Quý khách cửa hàng chúng tôi tổ chức chương trình khuyến mại nhằm chi ân khách hàng. "
        };
        return EmailTemplate.generateHtmlEmail("Thông báo khuyến mại từ " + EmailTemplate.SHOP_NAME, EmailTemplate.getBody(getContentPromotionCreated(promotion, user, contents)));
    }

    public static String getContentPromotionCreated(Promotion promotion, User user, String[] contents) {
        StringBuilder content = new StringBuilder();
        content.append(EmailTemplate.getDear(user.getFirstName() + " " + user.getLastName()))
                .append(EmailTemplate.formatContentWithNewLines(contents))
                .append(getPromotionInfo(promotion))
                .append(getThanhksAndFooter());
        return content.toString();
    }

    public static String getPromotionInfo(Promotion promotion) {
        StringBuilder infoPromotion = new StringBuilder();
        infoPromotion.append("<tr>")
                    .append("    <td style=\"padding: 10px 0 10px 0;\">")
                    .append("        Dưới đây là thông tin chi tiết về chương trình khuyến mại:")
                    .append("    </td>")
                    .append("</tr>")
                    .append("<tr>")
                    .append("    <td>")
                    .append("         <ul>")
                    .append("              <li style=\"color:black;\">Tên chương trình: <b>"+promotion.getName() +"</b></li>")
                    .append("              <li style=\"color:black;\">Thời gian : <b>" + XDate.getDateString(promotion.getStartDate()) + " - " + XDate.getDateString(promotion.getEndDate()) + "</b></li>");
        if (promotion.getIsPercent() == true) {
       infoPromotion.append("              <li style=\"color:black;\">Giảm giá: <b>" + promotion.getDiscount() +"%</b></li>");
        } else {
       infoPromotion.append("              <li style=\"color:black;\">Giảm giá: <b>" + XNumber.df.format(promotion.getDiscount()) +"</b></li>");
        }
       infoPromotion.append("              <li style=\"color:black;\">Để xem thông tin chi tiết về chương trình khuyến mại vui lòng click vào:</li>")
                    .append("         </ul>")
                    .append("    </td>")
                    .append("</tr>")
                    .append("<tr>")
                    .append("    <td align=\"center\" bgcolor=\"#FFFFFF\" style=\"padding: 10px 0 30px 0;\">")
                    .append("        <a href=\"https://sanvadio.buzz/").append("\" target=\"_blank\"")
                    .append("            style=\"color: #FFF; background-color: #d19c97; padding: 6px; text-decoration: none; border-radius: 5px;\">Xem")
                    .append("        chi tiết</a>")
                    .append("     </td>")
                    .append("</tr>");
        return infoPromotion.toString();
    }

    public static String getPromotionDetailInfo(Promotion promotion) {
        StringBuilder promotionDetailInfo = new StringBuilder();
        for (ProductDetail p : promotion.getProductDetails()) {
            promotionDetailInfo.append("<tr>")
                    .append("    <td>")
                    .append("        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">")
                    .append("            <tr>")
                    .append("                <td>")
                    .append("                    <img src=\"https://sanvadio.buzz/api/model/files/productDetailImages/").append(p.getImages()).append("\"")
                    .append("                     alt=\"Sanvadio\" width=\"80\" style=\"display: block;\" />")
                    .append("                 </td>")
                    .append("                 <td>")
                    .append("                    <ul>")
                    .append("                         <li>").append(p.getProduct().getId()).append("</li>")
                    .append(getVariationOptions(p))
                    .append("                    </ul>")
                    .append("                 </td>")
                    .append("                 <td>")
                    .append("                      Giá: ").append("<del>" + XNumber.df.format(p.getPriceDefault()) + "</del> <b> " + XNumber.df.format(p.getPriceInput()) + "</b>")
                    .append("                  </td>")
                    .append("             </tr>")
                    .append("       </table>")
                    .append("    </td>")
                    .append("</tr>");
        }
        return promotionDetailInfo.toString();
    }
    public static String getVariationOptions(ProductDetail productDetail) {
        StringBuilder variation = new StringBuilder();
        for (VariationOption v : productDetail.getVariationOptions()) {
            variation.append("<li>" + v.getVariation().getName() + ": " + v.getValue() + "</li>");
        }
        return variation.toString();
    }
    public static String getThanhksAndFooter() {
        StringBuilder footer = new StringBuilder();
        footer.append("<tr>")
                .append("    <td style=\"padding: 10px 0 10px 0;\">")
                .append("        Quý khách đóng góp ý kiến xin vui lòng liên hệ với chúng tôi qua địa chỉ email "+ EmailTemplate.SHOP_EMAIL +" hoặc số điện thoại "+ EmailTemplate.SHOP_PHONE +".")
                .append("    </td>")
                .append("</tr>")
                .append("<tr>")
                .append("    <td style=\"padding: 10px 0 10px 0;\">")
                .append("        Một lần nữa, chúng tôi xin chân thành cảm ơn Quý khách đã lựa chọn Sanvadio là địa điểm mua sắm của mình. Chúng tôi cam kết sẽ nỗ lực hết sức để mang đến cho Quý khách trải nghiệm mua sắm tuyệt vời và dịch vụ chất lượng cao.")
                .append("    </td>")
                .append("</tr>")
                .append("<tr>")
                .append("    <td style=\"padding: 10px 0 10px 0;\">")
                .append("        <div>Trân trọng,</div>")
                .append("        <div>Sanvadio</div>")
                .append("    </td>")
                .append("</tr>");
        return footer.toString();
    }
}
