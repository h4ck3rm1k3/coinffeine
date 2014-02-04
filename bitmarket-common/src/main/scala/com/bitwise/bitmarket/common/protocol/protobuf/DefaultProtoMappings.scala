package com.bitwise.bitmarket.common.protocol.protobuf

import java.math.BigDecimal
import java.util.Currency

import com.bitwise.bitmarket.common.protocol._
import com.bitwise.bitmarket.common.protocol.protobuf.{BitmarketProtobuf => msg}
import com.bitwise.bitmarket.common.PeerConnection
import com.bitwise.bitmarket.common.currency.{FiatAmount, BtcAmount}

/** Implicit conversion mappings for the protocol messages */
object DefaultProtoMappings {

  implicit val btcAmountMapping = new ProtoMapping[BtcAmount, msg.BtcAmount] {
    override def fromProtobuf(amount: msg.BtcAmount): BtcAmount =
      BtcAmount(BigDecimal.valueOf(amount.getValue, amount.getScale))

    override def toProtobuf(amount: BtcAmount): msg.BtcAmount = msg.BtcAmount.newBuilder
      .setValue(amount.amount.underlying().unscaledValue.longValue)
      .setScale(amount.amount.scale)
      .build
  }

  implicit val fiatAmountMapping = new ProtoMapping[FiatAmount, msg.FiatAmount] {

    override def fromProtobuf(amount: msg.FiatAmount): FiatAmount = FiatAmount(
      BigDecimal.valueOf(amount.getValue, amount.getScale),
      Currency.getInstance(amount.getCurrency)
    )

    override def toProtobuf(amount: FiatAmount): msg.FiatAmount = msg.FiatAmount.newBuilder
      .setValue(amount.amount.underlying().unscaledValue.longValue)
      .setScale(amount.amount.scale)
      .setCurrency(amount.currency.getCurrencyCode)
      .build
  }

  implicit val orderMapping = new ProtoMapping[Order, msg.Order] {

    override def fromProtobuf(order: msg.Order): Order = {
      Order(
        orderType = order.getType match {
          case msg.OrderType.BID => Bid
          case msg.OrderType.ASK => Ask
        },
        amount = ProtoMapping.fromProtobuf(order.getAmount),
        price = ProtoMapping.fromProtobuf(order.getPrice)
      )
    }

    override def toProtobuf(order: Order): msg.Order = msg.Order.newBuilder
      .setType(order.orderType match {
        case Bid => msg.OrderType.BID
        case Ask => msg.OrderType.ASK
      })
      .setAmount(ProtoMapping.toProtobuf(order.amount))
      .setPrice(ProtoMapping.toProtobuf(order.price))
      .build
  }

  implicit val orderCancellationMapping = new ProtoMapping[OrderCancellation, msg.OrderCancellation] {
    override def fromProtobuf(message: msg.OrderCancellation) = OrderCancellation(
      currency = Currency.getInstance(message.getCurrency)
    )
    override def toProtobuf(obj: OrderCancellation) = msg.OrderCancellation.newBuilder
      .setCurrency(obj.currency.getCurrencyCode)
      .build
  }

  implicit val orderMatchMapping = new ProtoMapping[OrderMatch, msg.OrderMatch] {

    override def fromProtobuf(orderMatch: msg.OrderMatch): OrderMatch = OrderMatch(
      exchangeId = orderMatch.getExchangeId,
      amount = ProtoMapping.fromProtobuf(orderMatch.getAmount),
      price = ProtoMapping.fromProtobuf(orderMatch.getPrice),
      buyer = PeerConnection.parse(orderMatch.getBuyer),
      seller = PeerConnection.parse(orderMatch.getSeller)
    )

    override def toProtobuf(orderMatch: OrderMatch): msg.OrderMatch = msg.OrderMatch.newBuilder
      .setExchangeId(orderMatch.exchangeId)
      .setAmount(ProtoMapping.toProtobuf(orderMatch.amount))
      .setPrice(ProtoMapping.toProtobuf(orderMatch.price))
      .setBuyer(orderMatch.buyer.toString)
      .setSeller(orderMatch.seller.toString)
      .build
  }
}
