package agoraa.app.forms_back.shared.enums

enum class WppGroupsEnum(private val groupId: String) {
    LANCAMENTO_COTACAO("6749b7eb8f2a5e3014639a2c"),
    CARGA_FECHADA("66aa84a8b959011aa7277c7a"),
    AMBIENTE_DE_TESTE("663a53e93b0a671bbcb23c93");

    fun getGroupId(): String {
        return groupId
    }
}