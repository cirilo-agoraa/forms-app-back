package agoraa.app.forms_back.repository

import agoraa.app.forms_back.model.UserModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserModel, Long>, JpaSpecificationExecutor<UserModel>{
    fun findByUsername(username: String): Optional<UserModel>
}