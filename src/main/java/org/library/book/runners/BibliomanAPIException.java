package org.library.book.runners;

import org.jsoup.Connection;

public class BibliomanAPIException extends RuntimeException {

    public BibliomanAPIException(String msg) {
        super(msg);
    }

    public BibliomanAPIException(String msg, Throwable t) {
        super(msg, t);
    }

    public BibliomanAPIException(Throwable t) {
        super(t);
    }


    public BibliomanAPIException(Connection.Response response) {
        super(respToString(response));
    }

    private static String respToString(Connection.Response response) {
        return "Bad response returned from URL: " + response.url()
                + "Code: " + response.statusCode()
                + " -> " + response.statusMessage();
    }
}
