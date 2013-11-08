package com.movie.feel.interfaces.observer

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 8/28/13
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Subject_I {

    public void addObserver(Observer_I o)

    public void removeObserver(Observer_I o)

    public String getStatus()

    public void setStatus(String status)

    public void notifyObserversWithCurrentStatus()

}