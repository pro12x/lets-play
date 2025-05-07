package com.cgl.lets_play.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User
{
    @Id
    private String id;

    @Field
    private String name;

    @Indexed(unique = true)
    @Field
    private String email;

    @JsonIgnore
    @Field
    private String password;

    @Field
    private String role;

    //private List<Product> products;
}
