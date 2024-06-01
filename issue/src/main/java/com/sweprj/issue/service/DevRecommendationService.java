package com.sweprj.issue.service;

import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.IssueEmbedding;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.repository.IssueEmbeddingRepository;
import com.sweprj.issue.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DevRecommendationService {
    private final IssueEmbeddingRepository issueEmbeddingRepository;

    private final UserRepository userRepository;

    private final GPT3EmbeddingService gpt3EmbeddingService;

    public DevRecommendationService(IssueEmbeddingRepository issueEmbeddingRepository, UserRepository userRepository, GPT3EmbeddingService gpt3EmbeddingService) {
        this.issueEmbeddingRepository = issueEmbeddingRepository;
        this.userRepository = userRepository;
        this.gpt3EmbeddingService = gpt3EmbeddingService;
    }

    public User recommendDeveloperForIssue(Issue issue) {
        double[] issueEmbedding = gpt3EmbeddingService.getEmbedding(issue.getTitle());

        List<User> users = userRepository.findAll();
        User bestMatch = null;
        double highestAverageSimilarity = -1;

        for (User user : users) {
            List<IssueEmbedding> userEmbeddings = issueEmbeddingRepository.findByUserUserId(user.getUserId());
            if (!userEmbeddings.isEmpty()) {
                double totalSimilarity = 0;
                for (IssueEmbedding userEmbedding : userEmbeddings) {
                    totalSimilarity += calculateCosineSimilarity(issueEmbedding, userEmbedding.getEmbeddingArray());
                }
                double averageSimilarity = totalSimilarity / userEmbeddings.size();
                if (averageSimilarity > highestAverageSimilarity) {
                    highestAverageSimilarity = averageSimilarity;
                    bestMatch = user;
                }
            }
        }

        return bestMatch;
    }

    private double calculateCosineSimilarity(double[] vecA, double[] vecB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vecA.length; i++) {
            dotProduct += vecA[i] * vecB[i];
            normA += Math.pow(vecA[i], 2);
            normB += Math.pow(vecB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
