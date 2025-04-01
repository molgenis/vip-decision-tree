package org.molgenis.vcf.decisiontree.filter;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.*;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataMapper;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;

@ExtendWith(MockitoExtension.class)
class ConsequenceAnnotatorImplTest {

    private String clazz;
    private Set<Label> labels;
    @Mock
    private VCFHeader vcfHeader;
    @Mock
    private Field vepfield;
    @Mock
    private VepMetadataMapper vepMetadataMapper;
    @Mock
    VCFInfoHeaderLine vepInfoHeaderLine;

    @BeforeEach
    void setUp() {
        clazz = "EXIT_NOW";
        Label label1 = Label.builder().id("id1").description("desc1").build();
        Label label2 = Label.builder().id("id2").description("desc2").build();
        labels = Set.of(label1, label2);
        when(vepInfoHeaderLine.getID()).thenReturn("CSQ");
        when(vepInfoHeaderLine.getDescription()).thenReturn("Consequence annotations from Ensembl VEP. Format: Allele|test1|test2");
        when(vcfHeader.getInfoHeaderLines()).thenReturn(List.of(vepInfoHeaderLine));
    }

    @Test
    void annotateLabelsPath() {
        Node node1 = mock(Node.class);
        when(node1.getId()).thenReturn("node1");
        Node node2 = mock(Node.class);
        when(node2.getId()).thenReturn("node2");
        Node node3 = mock(Node.class);
        when(node3.getId()).thenReturn("node3");
        when(vepMetadataMapper.map("CSQ", vcfHeader)).thenReturn(
                NestedHeaderLine.builder().nestedFields(Map.of("VIPC", new NestedField("VIPC", FieldType.INFO_VEP, ValueType.STRING, ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build(), 1, '|', 3, vepfield),
                                "VIPP", new NestedField("VIPP", FieldType.INFO_VEP, ValueType.STRING, ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build(), 1, '|', 4, vepfield),
                                "VIPL", new NestedField("VIPL", FieldType.INFO_VEP, ValueType.STRING, ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build(), 1, '|', 5, vepfield)))
                        .parentField(vepfield).build());

        List<Node> path = List.of(node1, node2, node3);
        Decision decision = Decision.builder().clazz(clazz).labels(labels).path(path).build();

        ConsequenceAnnotator annotator = new ConsequenceAnnotatorImpl(true, true, vcfHeader, vepMetadataMapper);
        assertEquals("test1||test3|EXIT_NOW|node1&node2&node3|id1&id2", annotator.annotate(decision, "test1||test3"));
    }

    @Test
    void annotateLabels() {
        when(vepMetadataMapper.map("CSQ", vcfHeader)).thenReturn(
                NestedHeaderLine.builder().nestedFields(Map.of("VIPC", new NestedField("VIPC", FieldType.INFO_VEP, ValueType.STRING, ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build(), 1, '|', 3, vepfield),
                                "VIPL", new NestedField("VIPL", FieldType.INFO_VEP, ValueType.STRING, ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build(), 1, '|', 4, vepfield)))
                        .parentField(vepfield).build());

        Decision decision = Decision.builder().clazz(clazz).labels(labels).path(emptyList()).build();
        ConsequenceAnnotator annotator = new ConsequenceAnnotatorImpl(true, false, vcfHeader, vepMetadataMapper);
        assertEquals("test1||test3|EXIT_NOW|id1&id2", annotator.annotate(decision, "test1||test3"));
    }

    @Test
    void annotate() {
        when(vepMetadataMapper.map("CSQ", vcfHeader)).thenReturn(
                NestedHeaderLine.builder().nestedFields(Map.of("VIPC", new NestedField("VIPC", FieldType.INFO_VEP, ValueType.STRING, ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build(), 1, '|', 1, vepfield))).parentField(vepfield).build());

        Decision decision = Decision.builder().clazz(clazz).labels(labels).path(emptyList()).build();
        ConsequenceAnnotator annotator = new ConsequenceAnnotatorImpl(false, false, vcfHeader, vepMetadataMapper);
        assertEquals("test1|EXIT_NOW|test3", annotator.annotate(decision, "test1|PREVIOUS_ANNOTATION|test3"));
    }
}