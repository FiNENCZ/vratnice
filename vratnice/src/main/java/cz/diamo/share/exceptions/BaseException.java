package cz.diamo.share.exceptions;

import org.apache.commons.lang3.StringUtils;

public class BaseException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String podrobnosti;
    private Boolean nonFatal = false;

    public Boolean getNonFatal() {
        return nonFatal;
    }

    public void setNonFatal(Boolean nonFatal) {
        this.nonFatal = nonFatal;
    }

    public String getPodrobnosti() {
        return podrobnosti;
    }

    public void setPodrobnosti(String podrobnosti) {
        this.podrobnosti = podrobnosti;
    }

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, String podrobnosti) {
        super(message);
        setPodrobnosti(podrobnosti);
    }

    public BaseException(String message, String podrobnosti, boolean nonFatal) {
        super(message);
        setPodrobnosti(podrobnosti);
        setNonFatal(nonFatal);
    }

    @Override
    public String toString() {
        String result = getMessage();
        if(!StringUtils.isBlank(podrobnosti))result+="\nPodrobnosti: "+podrobnosti;
        return result;
    }
}
