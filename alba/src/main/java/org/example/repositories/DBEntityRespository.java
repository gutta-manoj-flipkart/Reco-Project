package org.example.repositories;

import org.example.models.CompositeKey;
import org.example.models.DBEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBEntityRespository extends JpaRepository<DBEntity, CompositeKey> {
    // custom query to search to blog post by title or content
    //List<Blog> findByTitleContainingOrContentContaining(String text, String textAgain);
}
