package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.AuthorityModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : JpaRepository<AuthorityModel, Long>, PagingAndSortingRepository<AuthorityModel, Long> {
}