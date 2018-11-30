package fall2018.csc2017.slidingtiles;

public interface IObservable<T> {

    void addObserver(IObserver o);

    void deleteObserver(IObserver o);

    void notifyObservers();

    void clearChanged();

    boolean hasChanged();

    void setChanged();

}