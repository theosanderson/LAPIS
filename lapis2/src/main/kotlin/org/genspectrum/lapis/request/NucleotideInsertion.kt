package org.genspectrum.lapis.request

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import org.springframework.boot.jackson.JsonComponent
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

data class NucleotideInsertion(val position: Int, val insertions: String, val segment: String?) {
    companion object {
        fun fromString(nucleotideInsertion: String): NucleotideInsertion {
            val match = NUCLEOTIDE_INSERTION_REGEX.find(nucleotideInsertion)
                ?: throw IllegalArgumentException("Invalid nucleotide mutation: $nucleotideInsertion")

            val matchGroups = match.groups

            val position = matchGroups["position"]?.value?.toInt()
                ?: throw IllegalArgumentException(
                    "Invalid nucleotide insertion: $nucleotideInsertion: Did not find position",
                )

            val insertions = matchGroups["insertions"]?.value?.replace("?", ".*")
                ?: throw IllegalArgumentException(
                    "Invalid nucleotide insertion: $nucleotideInsertion: Did not find insertions",
                )

            return NucleotideInsertion(
                position,
                insertions,
                matchGroups["segment"]?.value,
            )
        }
    }
}

private val NUCLEOTIDE_INSERTION_REGEX =
    Regex(
        """^ins_((?<segment>[a-zA-Z0-9_-]+)(?=:):)?(?<position>\d+):(?<insertions>(([a-zA-Z?]|(\.\*))+))$""",
    )

@JsonComponent
class NucleotideInsertionDeserializer : JsonDeserializer<NucleotideInsertion>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
        NucleotideInsertion.fromString(p.valueAsString)
}

@Component
class StringToNucleotideInsertionConverter : Converter<String, NucleotideInsertion> {
    override fun convert(source: String) = NucleotideInsertion.fromString(source)
}
