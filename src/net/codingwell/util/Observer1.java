package net.codingwell.util;

public interface Observer1<T>
{
	void update(Observable1<T> o, T arg);
}
