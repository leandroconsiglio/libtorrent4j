/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.libtorrent4j.swig;

public class block_info_vector extends java.util.AbstractList<block_info> implements java.util.RandomAccess {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected block_info_vector(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(block_info_vector obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(block_info_vector obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtorrent_jni.delete_block_info_vector(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public block_info_vector(block_info[] initialElements) {
    this();
    reserve(initialElements.length);

    for (block_info element : initialElements) {
      add(element);
    }
  }

  public block_info_vector(Iterable<block_info> initialElements) {
    this();
    for (block_info element : initialElements) {
      add(element);
    }
  }

  public block_info get(int index) {
    return doGet(index);
  }

  public block_info set(int index, block_info e) {
    return doSet(index, e);
  }

  public boolean add(block_info e) {
    modCount++;
    doAdd(e);
    return true;
  }

  public void add(int index, block_info e) {
    modCount++;
    doAdd(index, e);
  }

  public block_info remove(int index) {
    modCount++;
    return doRemove(index);
  }

  protected void removeRange(int fromIndex, int toIndex) {
    modCount++;
    doRemoveRange(fromIndex, toIndex);
  }

  public int size() {
    return doSize();
  }

  public block_info_vector() {
    this(libtorrent_jni.new_block_info_vector__SWIG_0(), true);
  }

  public block_info_vector(block_info_vector other) {
    this(libtorrent_jni.new_block_info_vector__SWIG_1(block_info_vector.getCPtr(other), other), true);
  }

  public long capacity() {
    return libtorrent_jni.block_info_vector_capacity(swigCPtr, this);
  }

  public void reserve(long n) {
    libtorrent_jni.block_info_vector_reserve(swigCPtr, this, n);
  }

  public boolean isEmpty() {
    return libtorrent_jni.block_info_vector_isEmpty(swigCPtr, this);
  }

  public void clear() {
    libtorrent_jni.block_info_vector_clear(swigCPtr, this);
  }

  public block_info_vector(int count, block_info value) {
    this(libtorrent_jni.new_block_info_vector__SWIG_2(count, block_info.getCPtr(value), value), true);
  }

  private int doSize() {
    return libtorrent_jni.block_info_vector_doSize(swigCPtr, this);
  }

  private void doAdd(block_info x) {
    libtorrent_jni.block_info_vector_doAdd__SWIG_0(swigCPtr, this, block_info.getCPtr(x), x);
  }

  private void doAdd(int index, block_info x) {
    libtorrent_jni.block_info_vector_doAdd__SWIG_1(swigCPtr, this, index, block_info.getCPtr(x), x);
  }

  private block_info doRemove(int index) {
    return new block_info(libtorrent_jni.block_info_vector_doRemove(swigCPtr, this, index), true);
  }

  private block_info doGet(int index) {
    return new block_info(libtorrent_jni.block_info_vector_doGet(swigCPtr, this, index), false);
  }

  private block_info doSet(int index, block_info val) {
    return new block_info(libtorrent_jni.block_info_vector_doSet(swigCPtr, this, index, block_info.getCPtr(val), val), true);
  }

  private void doRemoveRange(int fromIndex, int toIndex) {
    libtorrent_jni.block_info_vector_doRemoveRange(swigCPtr, this, fromIndex, toIndex);
  }

}