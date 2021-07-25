package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the TakeNote type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "TakeNotes")
public final class TakeNote implements Model {
  public static final QueryField ID = field("TakeNote", "id");
  public static final QueryField NAME = field("TakeNote", "name");
  public static final QueryField PRIORITY = field("TakeNote", "priority");
  public static final QueryField DESCRIPTION = field("TakeNote", "description");
  public static final QueryField USER = field("TakeNote", "takeNoteUserId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="Priority") Priority priority;
  private final @ModelField(targetType="String") String description;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "takeNoteUserId", type = User.class) User user;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public Priority getPriority() {
      return priority;
  }
  
  public String getDescription() {
      return description;
  }
  
  public User getUser() {
      return user;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private TakeNote(String id, String name, Priority priority, String description, User user) {
    this.id = id;
    this.name = name;
    this.priority = priority;
    this.description = description;
    this.user = user;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      TakeNote takeNote = (TakeNote) obj;
      return ObjectsCompat.equals(getId(), takeNote.getId()) &&
              ObjectsCompat.equals(getName(), takeNote.getName()) &&
              ObjectsCompat.equals(getPriority(), takeNote.getPriority()) &&
              ObjectsCompat.equals(getDescription(), takeNote.getDescription()) &&
              ObjectsCompat.equals(getUser(), takeNote.getUser()) &&
              ObjectsCompat.equals(getCreatedAt(), takeNote.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), takeNote.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getPriority())
      .append(getDescription())
      .append(getUser())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("TakeNote {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("priority=" + String.valueOf(getPriority()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("user=" + String.valueOf(getUser()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static TakeNote justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new TakeNote(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      priority,
      description,
      user);
  }
  public interface NameStep {
    BuildStep name(String name);
  }
  

  public interface BuildStep {
    TakeNote build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep priority(Priority priority);
    BuildStep description(String description);
    BuildStep user(User user);
  }
  

  public static class Builder implements NameStep, BuildStep {
    private String id;
    private String name;
    private Priority priority;
    private String description;
    private User user;
    @Override
     public TakeNote build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new TakeNote(
          id,
          name,
          priority,
          description,
          user);
    }
    
    @Override
     public BuildStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep priority(Priority priority) {
        this.priority = priority;
        return this;
    }
    
    @Override
     public BuildStep description(String description) {
        this.description = description;
        return this;
    }
    
    @Override
     public BuildStep user(User user) {
        this.user = user;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String name, Priority priority, String description, User user) {
      super.id(id);
      super.name(name)
        .priority(priority)
        .description(description)
        .user(user);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder priority(Priority priority) {
      return (CopyOfBuilder) super.priority(priority);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
  }
  
}
