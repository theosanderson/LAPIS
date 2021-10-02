/*
 * This file is generated by jOOQ.
 */
package org.jooq.lapis.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.lapis.DefaultSchema;
import org.jooq.lapis.Keys;
import org.jooq.lapis.tables.records.YMainSequenceRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class YMainSequence extends TableImpl<YMainSequenceRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>y_main_sequence</code>
     */
    public static final YMainSequence Y_MAIN_SEQUENCE = new YMainSequence();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<YMainSequenceRecord> getRecordType() {
        return YMainSequenceRecord.class;
    }

    /**
     * The column <code>y_main_sequence.id</code>.
     */
    public final TableField<YMainSequenceRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>y_main_sequence.seq_original_compressed</code>.
     */
    public final TableField<YMainSequenceRecord, byte[]> SEQ_ORIGINAL_COMPRESSED = createField(DSL.name("seq_original_compressed"), SQLDataType.BLOB, this, "");

    /**
     * The column <code>y_main_sequence.seq_aligned_compressed</code>.
     */
    public final TableField<YMainSequenceRecord, byte[]> SEQ_ALIGNED_COMPRESSED = createField(DSL.name("seq_aligned_compressed"), SQLDataType.BLOB, this, "");

    /**
     * The column <code>y_main_sequence.aa_mutations</code>.
     */
    public final TableField<YMainSequenceRecord, String> AA_MUTATIONS = createField(DSL.name("aa_mutations"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>y_main_sequence.nuc_substitutions</code>.
     */
    public final TableField<YMainSequenceRecord, String> NUC_SUBSTITUTIONS = createField(DSL.name("nuc_substitutions"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>y_main_sequence.nuc_deletions</code>.
     */
    public final TableField<YMainSequenceRecord, String> NUC_DELETIONS = createField(DSL.name("nuc_deletions"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>y_main_sequence.nuc_insertions</code>.
     */
    public final TableField<YMainSequenceRecord, String> NUC_INSERTIONS = createField(DSL.name("nuc_insertions"), SQLDataType.CLOB, this, "");

    private YMainSequence(Name alias, Table<YMainSequenceRecord> aliased) {
        this(alias, aliased, null);
    }

    private YMainSequence(Name alias, Table<YMainSequenceRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>y_main_sequence</code> table reference
     */
    public YMainSequence(String alias) {
        this(DSL.name(alias), Y_MAIN_SEQUENCE);
    }

    /**
     * Create an aliased <code>y_main_sequence</code> table reference
     */
    public YMainSequence(Name alias) {
        this(alias, Y_MAIN_SEQUENCE);
    }

    /**
     * Create a <code>y_main_sequence</code> table reference
     */
    public YMainSequence() {
        this(DSL.name("y_main_sequence"), null);
    }

    public <O extends Record> YMainSequence(Table<O> child, ForeignKey<O, YMainSequenceRecord> key) {
        super(child, key, Y_MAIN_SEQUENCE);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<YMainSequenceRecord> getPrimaryKey() {
        return Keys.Y_MAIN_SEQUENCE_STAGING_PKEY;
    }

    @Override
    public List<UniqueKey<YMainSequenceRecord>> getKeys() {
        return Arrays.<UniqueKey<YMainSequenceRecord>>asList(Keys.Y_MAIN_SEQUENCE_STAGING_PKEY);
    }

    @Override
    public YMainSequence as(String alias) {
        return new YMainSequence(DSL.name(alias), this);
    }

    @Override
    public YMainSequence as(Name alias) {
        return new YMainSequence(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public YMainSequence rename(String name) {
        return new YMainSequence(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public YMainSequence rename(Name name) {
        return new YMainSequence(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<Integer, byte[], byte[], String, String, String, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
