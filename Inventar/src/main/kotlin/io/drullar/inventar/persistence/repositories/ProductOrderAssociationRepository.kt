package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.Relation
import io.drullar.inventar.persistence.model.ProductOrderAssociationModel
import io.drullar.inventar.persistence.model.id.ProductOrderKey
import io.drullar.inventar.persistence.schema.associative.ProductCategoriesAssociation.productId
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert

@Relation
object ProductOrderAssociationRepository :
    AbstractPersistenceRepository<ProductOrderAssociation, ProductOrderAssociationModel, ProductOrderKey>(
        ProductOrderAssociation
    ) {
    override fun save(model: ProductOrderAssociationModel): ProductOrderKey = withTransaction {
        table.insert {
            it[productId] = model.productOrderKey.productId
            it[orderId] = model.productOrderKey.orderId
            it[orderedAmount] = model.orderedAmount
        }

        model.productOrderKey
    }

    override fun transformResultRowToModel(row: ResultRow): ProductOrderAssociationModel {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: ProductOrderKey) {
        TODO("Not yet implemented")
    }

    override fun findById(id: ProductOrderKey): ProductOrderAssociationModel? {
        TODO("Not yet implemented")
    }

    override fun update(id: ProductOrderKey, model: ProductOrderAssociationModel) {
        TODO("Not yet implemented")
    }

}