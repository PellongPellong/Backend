package com.orumi.pelongpelong.common.exception

class PelongException(
        val messageType: MessageType,
) : RuntimeException(messageType.message)
