package br.pucminas.damd;

public class ResponseClass {
    String responseString;

    public ResponseClass(String responseString) {
        this.responseString = responseString;
    }

    public ResponseClass() {
    }

    public String getResponseString() {
        return responseString;
    }

    public void setGreetings(String responseString) {
        this.responseString = responseString;
    }


}
