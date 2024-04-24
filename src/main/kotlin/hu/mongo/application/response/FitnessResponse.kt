package hu.mongo.application.response

import hu.mongo.domain.entity.FitnessDetails

data class FitnessResponse(
    val id: String,
    val exerciseType: String,
    val notes: String,
    val details: FitnessDetails
)