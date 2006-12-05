package com.intellij.localvcs;

import static com.intellij.localvcs.Difference.Kind.*;

import java.io.IOException;

public class FileEntry extends Entry {
  // todo change String to ByteArray or something else
  private String myContent;

  public FileEntry(Integer id, String name, String content, Long timestamp) {
    super(id, name, timestamp);
    myContent = content;
  }

  public FileEntry(Stream s) throws IOException {
    super(s);
    myContent = s.readString();
  }

  @Override
  public void write(Stream s) throws IOException {
    super.write(s);
    s.writeString(myContent);
  }

  @Override
  public String getContent() {
    return myContent;
  }

  @Override
  public FileEntry copy() {
    return new FileEntry(myId, myName, myContent, null); // todo BUG in timestamp copying!!!
  }

  @Override
  public Entry withContent(String newContent, Long timestamp) {
    return new FileEntry(myId, myName, newContent, timestamp);
  }

  @Override
  public Difference getDifferenceWith(Entry e) {
    // todo calculate difference by timestamp;
    boolean modified = !myName.equals(e.getName()) || !myContent.equals(e.getContent());
    return new Difference(true, modified ? MODIFIED : NOT_MODIFIED, this, e);
  }

  @Override
  protected Difference asCreatedDifference() {
    return new Difference(true, CREATED, null, this);
  }

  @Override
  protected Difference asDeletedDifference() {
    return new Difference(true, DELETED, this, null);
  }
}
