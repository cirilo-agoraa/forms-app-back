package agoraa.app.forms_back.users.users.repository

import agoraa.app.forms_back.users.users.model.UserModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<UserModel, Long>, JpaSpecificationExecutor<UserModel> {
    fun findByUsername(username: String): Optional<UserModel>
}