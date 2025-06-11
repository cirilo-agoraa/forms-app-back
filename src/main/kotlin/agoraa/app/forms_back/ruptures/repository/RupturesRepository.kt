package agoraa.app.forms_back.ruptures.repository

import agoraa.app.forms_back.ruptures.model.RupturaModel
import org.springframework.data.jpa.repository.JpaRepository

interface RupturaRepository : JpaRepository<RupturaModel, Long>