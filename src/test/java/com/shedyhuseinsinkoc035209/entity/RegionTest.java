package com.shedyhuseinsinkoc035209.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegionTest {

    @Test
    void defaultConstructor_shouldCreateRegionWithActiveTrue() {
        Region region = new Region();

        assertThat(region.getId()).isNull();
        assertThat(region.getActive()).isTrue();
    }

    @Test
    void constructor_withFields_shouldSetFields() {
        Region region = new Region(1, "Cuiaba", true);

        assertThat(region.getExternalId()).isEqualTo(1);
        assertThat(region.getName()).isEqualTo("Cuiaba");
        assertThat(region.getActive()).isTrue();
    }

    @Test
    void deactivate_shouldSetActiveToFalse() {
        Region region = new Region(1, "Cuiaba", true);

        region.deactivate();

        assertThat(region.getActive()).isFalse();
    }

    @Test
    void hasNameChanged_shouldReturnTrueWhenNameDiffers() {
        Region region = new Region(1, "Cuiaba", true);

        assertThat(region.hasNameChanged("Varzea Grande")).isTrue();
    }

    @Test
    void hasNameChanged_shouldReturnFalseWhenNameIsSame() {
        Region region = new Region(1, "Cuiaba", true);

        assertThat(region.hasNameChanged("Cuiaba")).isFalse();
    }
}
