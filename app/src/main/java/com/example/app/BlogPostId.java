package com.example.app;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.Exclude;
// comments added

public class BlogPostId {

    // means we exclude the below string from being used in firestore
    @Exclude
    public String BlogPostId;

    // generic method and means that T can be of any type that is a subclass of BlogPostId
    // In map structures <K, V> is used which means "Key" and "Value" type
    // this method returns the same generic type T, and since it is a subclass of BlogPostId therefore
    // it also has the BlogPostId parameter included
    // returns this instance of the generic type T, which should be returned as type T

    public <T extends BlogPostId> T withId(@NonNull final String id) {
        this.BlogPostId = id;
        return (T) this;
    }
}