package com.sweprj.issue.service;

import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.IssueEmbedding;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.repository.IssueEmbeddingRepository;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private final GPT3EmbeddingService gpt3EmbeddingService;

    private final IssueEmbeddingRepository issueEmbeddingRepository;

    public EmbeddingService(GPT3EmbeddingService gpt3EmbeddingService, IssueEmbeddingRepository issueEmbeddingRepository) {
        this.gpt3EmbeddingService = gpt3EmbeddingService;
        this.issueEmbeddingRepository = issueEmbeddingRepository;
    }

    public void createIssueEmbedding(Issue issue, User user) {
        double[] embedding = gpt3EmbeddingService.getEmbedding(issue.getTitle());
        IssueEmbedding issueEmbedding = new IssueEmbedding();
        issueEmbedding.setIssue(issue);
        issueEmbedding.setUser(user);
        issueEmbedding.setEmbedding(embedding);
        issueEmbeddingRepository.save(issueEmbedding);
    }
}
