package com.orange.common.template;

public class EmailTemplate {
    public static final String LOGO__PATH = "https://sanvadio.buzz/api/model/files/appImage/logo.png";
    public static final String SHOP_NAME = "Sanvadio";
    public static final String SHOP_PHONE = "0988888888";
    public static final String SHOP_EMAIL = "sanvadio.buzz@gmail.com";
    public static String generateHtmlEmail(String title, String body) {
        String htmlEmail = "";
        htmlEmail += "<!DOCTYPE html";
        htmlEmail += "    PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
        htmlEmail += "<html xmlns=\"http://www.w3.org/1999/xhtml\">";
        htmlEmail += "<head>";
        htmlEmail += "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />";
        htmlEmail += "    <title>" + title + "</title>";
        htmlEmail += "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />";
        htmlEmail += "</head>";
        htmlEmail += "<body style=\"margin: 0; padding: 0;\">";
        htmlEmail += "    <table bgcolor=\"#ffffff\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"";
        htmlEmail += "        style=\"border-collapse: collapse;\">";
        htmlEmail += getLogo();
        htmlEmail += body;
        htmlEmail += "      </table>";
        htmlEmail += "  </body>";
        htmlEmail += "</html>";
        return htmlEmail;
    }
    public static String getBody(String content) {
        String body = "";
        body += "<tr>";
        body += "   <td bgcolor=\"#ffffff\" style=\"padding: 40px 30px 40px 30px;\">";
        body += "       <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">";
        body += content;
        body += "       </table>";
        body += "   </td>";
        body += "</tr>";
        return body;
    }
    public static String getDear(String receiver) {
        String dear = "";
        dear += "<tr>";
        dear += "   <td>";
        dear += "       Kính gửi " + receiver + ",";
        dear += "   </td>";
        dear += "</tr>";
        return dear;
    }
    public static String getLogo() {
        String logo = "";
        logo += "<tr>";
        logo += "    <td align=\"center\" bgcolor=\"#FFFFFF\" style=\"padding: 40px 0 30px 0;\">";
        logo += "        <img src=\"" + LOGO__PATH + "\"";
        logo += "        alt=\"Sanvadio\" width=\"100\" style=\"display: block;\" />";
        logo += "    </td>";
        logo += "</tr>";
        return logo;
    }

    /**
     * Phương thức này được sử dụng để định dạng nội dung với các dòng mới trong một mảng chuỗi đầu vào.
     * Nội dung đầu vào sẽ được chuyển đổi thành chuỗi HTML trong định dạng bảng HTML với mỗi phần tử trong mảng chuỗi đầu vào
     * được hiển thị trong một hàng của bảng.
     *
     * @param contents Mảng chuỗi đầu vào chứa các nội dung cần được định dạng.
     * @return Chuỗi kết quả đã được định dạng trong định dạng bảng HTML với các dòng mới.
     */
    public static String formatContentWithNewLines(String[] contents) {
        StringBuilder content = new StringBuilder();
        for (String s : contents) {
            content.append("<tr>")
                    .append("    <td style=\"padding: 10px 0 10px 0;\">")
                    .append(        s)
                    .append("    </td>")
                    .append("</tr>");
        }
        return content.toString();
    }

    public static String getThanhksAndFooter() {
        StringBuilder footer = new StringBuilder();
        footer.append("<tr>")
                .append("    <td style=\"padding: 10px 0 10px 0;\">")
                .append("        Quý khách vui lòng kiểm tra kỹ thông tin trên để đảm bảo đúng với đơn hàng của mình. Nếu có bất kỳ thắc mắc nào, xin vui lòng liên hệ với chúng tôi qua địa chỉ email "+ SHOP_EMAIL +" hoặc số điện thoại "+ SHOP_PHONE +".")
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