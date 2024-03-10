package com.orange.common.template;

import com.orange.utils.XDate;
import com.orange.utils.XNumber;
import com.orange.domain.model.Order;
import com.orange.domain.model.OrderDetail;
import com.orange.domain.model.ProductDetail;
import com.orange.domain.model.VariationOption;

import java.util.Set;

public class EmailTemplateOrder {
    public static String generateHtmlOrderCreatedEmail(Order order) {
        String[] contents = new String[]{
                "Chúng tôi xin gửi lời cảm ơn chân thành đến Quý khách đã lựa chọn mua sắm tại "+ EmailTemplate.SHOP_NAME +". Chúng tôi rất trân trọng sự ủng hộ của Quý khách và mong muốn đáp ứng tốt nhất nhu cầu của Quý khách.",
                "Tuy nhiên, để đảm bảo chất lượng sản phẩm và dịch vụ, chúng tôi cần Quý khách kiểm tra lại đơn hàng của mình. Đây là bước cần thiết để đảm bảo rằng đơn hàng đã được xử lý đúng và hoàn thiện theo yêu cầu của Quý khách."
        };
        return EmailTemplate.generateHtmlEmail("Yêu cầu kiểm tra đơn hàng mới tại "+ EmailTemplate.SHOP_NAME, EmailTemplate.getBody(getContentOrder(order, contents)));
    }
    public static String generateHtmlOrderAfterConfirmAndShippingEmail(Order order) {
        String[] contents = new String[]{
                "Chúng tôi xin gửi lời cảm ơn chân thành tới Quý khách vì đã đặt mua hàng tại " + EmailTemplate.SHOP_NAME + "! Chúng tôi rất trân trọng sự tin tưởng và ủng hộ của Quý khách.",
                "Chúng tôi xin thông báo rằng đơn hàng của Quý khách đã được xác nhận và đang được xử lý bởi đội ngũ nhân viên của chúng tôi. Sau khi đơn hàng của Quý khách được đóng gói và chuẩn bị sẽ được chuyển giao cho dịch vụ vận chuyển trong thời gian sớm nhất."
        };
        return EmailTemplate.generateHtmlEmail("Thông báo xác nhận đơn hàng từ " + EmailTemplate.SHOP_NAME, EmailTemplate.getBody(getContentOrder(order, contents)));
    }
    public static String generateHtmlOrderAfterCompleted(Order order) {
        String[] contents = new String[]{
                "Chúng tôi xin gửi lời cảm ơn đến Quý khách đã tin tưởng và mua sắm tại " + EmailTemplate.SHOP_NAME + ". Chúng tôi xin thông báo rằng đơn hàng của Quý khách đã được giao thành công và đã đến nơi đích.",
                "Chúng tôi hy vọng Quý khách đã hài lòng với sản phẩm/sản phẩm đã mua từ chúng tôi. Đội ngũ của chúng tôi đã cố gắng hết sức để đảm bảo đơn hàng của Quý khách được giao đúng thời gian và đúng chất lượng."
        };
        return EmailTemplate.generateHtmlEmail("Thông báo về việc giao hàng thành công từ " + EmailTemplate.SHOP_NAME, EmailTemplate.getBody(getContentOrder(order, contents)));
    }

    public static String generateHtmlOrderCancelByDeleveriFail(Order order) {
        String[] contents = new String[]{
                "Chúng tôi gửi email này để thông báo rằng đơn hàng của bạn tại " + EmailTemplate.SHOP_NAME + " đã bị hủy do giao hàng không thành công. Theo thông tin từ đối tác vận chuyển, đơn hàng của bạn không thể được giao thành công do khách hàng chưa nhận hoặc không có mặt tại địa chỉ giao hàng vào lần giao hàng trước đó.",
                "Chúng tôi xin chân thành xin lỗi vì sự bất tiện này. Để đảm bảo chất lượng dịch vụ và đáp ứng đúng yêu cầu của quý khách, đơn hàng của bạn đã được hủy và số tiền đã thanh toán sẽ được hoàn lại cho bạn trong thời gian sớm nhất nếu là đơn hàng đã thanh toán."
        };
        return EmailTemplate.generateHtmlEmail("[THÔNG BÁO] Hủy đơn hàng do giao hàng không thành công", EmailTemplate.getBody(getContentOrder(order, contents)));
    }
    public static String generateHtmlOrderAfterRequestCancel(Order order) {
        String[] contents = new String[]{
                "Chúng tôi xin thông báo rằng chúng tôi đã nhận được yêu cầu hủy đơn hàng mà Quý khách vừa đặt tại "+ EmailTemplate.SHOP_NAME +". Chúng tôi xin xác nhận rằng yêu cầu hủy đã được ghi nhận và đang được xử lý bởi đội ngũ chăm sóc khách hàng của chúng tôi.",
                "Chúng tôi rất tiếc vì bất kỳ sự bất tiện nào mà yêu cầu hủy đơn hàng này có thể gây ra. Chúng tôi luôn đặt lợi ích của khách hàng lên hàng đầu và đang nỗ lực để đáp ứng mọi yêu cầu của khách hàng một cách nhanh chóng và hiệu quả."
        };
        return EmailTemplate.generateHtmlEmail("Thông báo về yêu cầu hủy đơn hàng từ " + EmailTemplate.SHOP_NAME, EmailTemplate.getBody(getContentOrder(order, contents)));
    }
    public static String generateHtmlOrderAfterApproveCancel(Order order) {
        String[] contents = new String[]{
                "Chúng tôi xin xác nhận rằng yêu cầu hủy đơn hàng mà Quý khách vừa gửi tới "+ EmailTemplate.SHOP_NAME +" đã được xử lý thành công. Đơn hàng của Quý khách đã được hủy và chúng tôi đã thực hiện các bước cần thiết để hoàn trả lại số tiền thanh toán cho phương thức thanh toán ban đầu của Quý khách.",
                "Chúng tôi rất tiếc vì bất kỳ sự bất tiện nào mà Quý khách có thể đã gặp phải. Chúng tôi luôn cố gắng cung cấp dịch vụ tốt nhất cho khách hàng của mình, và chúng tôi rất trân trọng sự ủng hộ và niềm tin của Quý khách đối với " + EmailTemplate.SHOP_NAME +"."
        };
        return EmailTemplate.generateHtmlEmail("Xác nhận yêu cầu hủy đơn hàng từ " + EmailTemplate.SHOP_NAME, EmailTemplate.getBody(getContentOrder(order, contents)));
    }
    public static String generateHtmlOrderAfterDeliveryEmail(Order order) {
        String[] contents = new String[]{
                "Chúng tôi gửi thông báo này để thông báo rằng đơn hàng của Quý khách tại "+ EmailTemplate.SHOP_NAME +" đang được giao tới Quý khách. Chúng tôi rất hân hạnh được phục vụ Quý khách và xin nhắc nhở Quý khách lưu ý nhận hàng đúng thời gian đã hẹn.",
                "Hãy đảm bảo rằng Quý khách hoặc người đại diện của Quý khách sẽ có mặt để nhận hàng vào thời gian đã được đặt trước. Điều này sẽ giúp đảm bảo việc giao hàng diễn ra thuận lợi và đúng thời hạn."
        };
        return EmailTemplate.generateHtmlEmail("Xác nhận đơn hàng từ "+ EmailTemplate.SHOP_NAME +" đã được bàn giao cho phía giao hàng", EmailTemplate.getBody(getContentOrder(order, contents)));
    }
    public static String generateHtmlOrderAfterPackaging(Order order) {
        String[] contents = new String[]{
                "Chúng tôi xin gửi lời cảm ơn chân thành đến Quý khách vì đã đặt mua hàng tại "+ EmailTemplate.SHOP_NAME +". Chúng tôi xin thông báo rằng đơn hàng của Quý khách đang được đóng gói và sẽ sớm được chuyển đến địa chỉ giao hàng mà Quý khách đã cung cấp.",
                "Chúng tôi đang đảm bảo rằng sản phẩm của Quý khách sẽ được đóng gói cẩn thận và an toàn để đảm bảo đến tay Quý khách trong tình trạng hoàn hảo. Chúng tôi đồng thời đang làm việc chặt chẽ với đối tác vận chuyển để đảm bảo đơn hàng của Quý khách được vận chuyển đúng thời gian dự kiến."
        };
        return EmailTemplate.generateHtmlEmail("Xác nhận đơn hàng từ "+ EmailTemplate.SHOP_NAME +" đã được bàn giao cho phía giao hàng", EmailTemplate.getBody(getContentOrder(order, contents)));
    }
    public static String generateHtmlOrderAfterTransport(Order order) {
        String[] contents = new String[]{
                "Chúng tôi xin gửi lời cảm ơn chân thành đến Quý khách vì đã đặt mua hàng tại "+ EmailTemplate.SHOP_NAME +". Chúng tôi xin thông báo rằng đơn hàng của Quý khách đã được bàn giao cho phía giao hàng và sẽ sớm được chuyển đến địa chỉ giao hàng mà Quý khách đã cung cấp.",
                "Thông tin chi tiết về đơn hàng của Quý khách như số đơn hàng, sản phẩm đã đặt mua, giá cả, và địa chỉ giao hàng đã được xác nhận trong hệ thống của chúng tôi. Quý khách có thể theo dõi tiến trình giao hàng của đơn hàng này thông qua đường link theo dõi đơn hàng được cung cấp trong email này."
        };
        return EmailTemplate.generateHtmlEmail("Xác nhận đơn hàng từ "+ EmailTemplate.SHOP_NAME +" đã được bàn giao cho phía giao hàng", EmailTemplate.getBody(getContentOrder(order, contents)));
    }
    public static String generateHtmlOrderAfterRequestReturlEmail(Order order) {
        String[] contents = new String[]{
                "Chúng tôi xin gửi lời cảm ơn chân thành đến Quý khách vì đã đặt mua hàng tại "+ EmailTemplate.SHOP_NAME +". Chúng tôi đã nhận được yêu cầu hoàn hàng của Quý khách và xin thông báo rằng yêu cầu này đã được xử lý và đang được xác nhận.",
                "Đội ngũ chúng tôi đang xem xét yêu cầu của Quý khách và sẽ tiến hành hoàn hàng trong thời gian sớm nhất có thể. Sau khi hoàn hàng hoàn tất, Quý khách sẽ nhận được hoàn tiền vào tài khoản đã sử dụng để thanh toán ban đầu hoặc qua phương thức thanh toán đã được đề xuất trong yêu cầu hoàn hàng.",
                "Chúng tôi xin cam kết đem đến dịch vụ hỗ trợ hoàn hàng nhanh chóng và chuyên nghiệp, và sẽ giải quyết mọi vấn đề liên quan đến hoàn hàng của Quý khách một cách nhanh chóng và hiệu quả."
        };
        return EmailTemplate.generateHtmlEmail("Xác nhận yêu cầu hoàn hàng từ "+ EmailTemplate.SHOP_NAME, EmailTemplate.getBody(getContentOrder(order, contents)));
    }
    public static String getContentOrder(Order order, String[] contents) {
        StringBuilder content = new StringBuilder();
        content.append(EmailTemplate.getDear(order.getConsigneeName()))
                .append(EmailTemplate.formatContentWithNewLines(contents))
                .append("    <td align=\"center\" bgcolor=\"#FFFFFF\" style=\"padding: 10px 0 30px 0;\">")
                .append("        <a href=\"https://sanvadio.buzz/user/tracking-page/").append(order.getId()).append("\" target=\"_blank\"")
                .append("            style=\"color: #FFF; background-color: #d19c97; padding: 6px; text-decoration: none; border-radius: 5px;\">Xem")
                .append("        chi tiết</a>")
                .append("        <hr/>")
                .append("     </td>")
                .append("</tr>")
                .append(getOrderInfo(order))
                .append(EmailTemplate.getThanhksAndFooter());
        return content.toString();
    }
    public static String getOrderInfo(Order order) {
        StringBuilder infoOrder = new StringBuilder();
        infoOrder.append("<tr>")
                .append("    <td style=\"padding: 10px 0 10px 0;\">")
                .append("        Dưới đây là thông tin chi tiết về đơn hàng của Quý khách:")
                .append("    </td>")
                .append("</tr>")
                .append("<tr>")
                .append("    <td>")
                .append("         <ul>")
                .append("              <li>Mã đơn hàng: " + order.getId() + "</li>")
                .append("              <li>Ngày đặt hàng: " + XDate.sdfDMY.format(order.getCreateDate()) + "</li>");
        if (order.getVoucherDiscount() != null) {
            infoOrder.append("              <li>Giảm giá: " + XNumber.df.format(order.getVoucherDiscount()) + "</li>");
        }
        if (order.getShippingFee() != null) {
            infoOrder.append("              <li>Phí vận chuyển: " + XNumber.df.format(order.getShippingFee()) + "</li>");
        }
        infoOrder.append("              <li>Tổng tiền hàng: " + XNumber.df.format(order.getOrderTotal()) + "</li>")
                .append("         </ul>")
                .append("    </td>")
                .append("</tr>")
                .append(getOrderDetailInfo(order.getOrderDetails()));
        return infoOrder.toString();
    }
    public static String getOrderDetailInfo(Set<OrderDetail> orderDetails) {
        StringBuilder orderDetailInfo = new StringBuilder();
        for (OrderDetail o : orderDetails) {
            orderDetailInfo.append("<tr>")
                    .append("    <td>")
                    .append("        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">")
                    .append("            <tr>")
                    .append("                <td>")
                    .append("                    <img src=\"https://sanvadio.buzz/api/model/files/productDetailImages/").append(o.getProductDetail().getImages()).append("\"")
                    .append("                     alt=\"Sanvadio\" width=\"80\" style=\"display: block;\" />")
                    .append("                 </td>")
                    .append("                 <td>")
                    .append("                    <ul>")
                    .append("                         <li>").append(o.getProductDetail().getProduct().getName()).append("</li>")
                    .append(getVariationOptions(o.getProductDetail()))
                    .append("                    </ul>")
                    .append("                 </td>")
                    .append("                 <td>")
                    .append("                      Giá: ").append(XNumber.df.format(o.getPrice()))
                    .append("                  </td>")
                    .append("             </tr>")
                    .append("       </table>")
                    .append("    </td>")
                    .append("</tr>");
        }
        return orderDetailInfo.toString();
    }
    public static String getVariationOptions(ProductDetail productDetail) {
        StringBuilder variation = new StringBuilder();
        for (VariationOption v : productDetail.getVariationOptions()) {
            variation.append("<li>" + v.getVariation().getName() + ": " + v.getValue() + "</li>");
        }
        return variation.toString();
    }
}
