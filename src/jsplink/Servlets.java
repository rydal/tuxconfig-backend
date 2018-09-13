package jsplink;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public final class Servlets {

    private Servlets() {}

    public static Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }

        return null;
    }

}
