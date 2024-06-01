package com.sweprj.issue.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueEmbedding {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    @CollectionTable(name = "embedding_values", joinColumns = @JoinColumn(name = "issue_embedding_id"))
    @Column(name = "value")
    private List<Double> embedding;

    public void setEmbedding(double[] embeddingArray) {
        this.embedding = Arrays.stream(embeddingArray).boxed().collect(Collectors.toList());
    }

    public double[] getEmbeddingArray() {
        return embedding.stream().mapToDouble(Double::doubleValue).toArray();
    }
}
