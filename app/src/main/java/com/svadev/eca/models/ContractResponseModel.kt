package com.svadev.eca.models


class ContractResponseModel (
    val buyout : Double? = null,
    val collateral : Double? = null,
    val contract_id : Int? = null,
    val date_expired: String? = null,
    val date_issued: String? = null,
    val days_to_complete: Int? = null,
    val end_location_id: Long? = null,
    val for_corporation: Boolean? = null,
    val issuer_corporation_id: Int? = null,
    val issuer_id: Int? = null,
    val price: Double? = null,
    val reward: Double? = null,
    val start_location_id: Long? = null,
    val title: String? = null,
    val type: String? = null,
    val volume: Double? = null
    )