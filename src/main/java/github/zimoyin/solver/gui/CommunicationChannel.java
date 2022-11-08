package github.zimoyin.solver.gui;

public interface CommunicationChannel<T>{
    public T getValue();
    public boolean setValue(T value);
}
