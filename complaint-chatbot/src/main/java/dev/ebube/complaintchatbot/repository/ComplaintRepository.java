package dev.ebube.complaintchatbot.repository;

import dev.ebube.complaintchatbot.entity.Complaint;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {}
