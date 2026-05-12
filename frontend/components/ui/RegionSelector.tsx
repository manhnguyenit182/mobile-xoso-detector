import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { Brand } from '../../constants/theme';
import { Region, REGION_LABELS } from '../../constants/data';

interface RegionSelectorProps {
  region: Region;
  onRegionChange: (region: Region) => void;
}

export function RegionSelector({ region, onRegionChange }: RegionSelectorProps) {
  return (
    <View style={styles.regionRow}>
      {(Object.keys(REGION_LABELS) as Region[]).map((r) => (
        <TouchableOpacity
          key={r}
          style={[styles.regionChip, region === r && styles.regionChipActive]}
          onPress={() => onRegionChange(r)}
        >
          <Text style={[styles.regionChipText, region === r && styles.regionChipTextActive]}>
            {REGION_LABELS[r]}
          </Text>
        </TouchableOpacity>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  regionRow: {
    flexDirection: 'row',
    gap: 10,
  },
  regionChip: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 12,
    backgroundColor: Brand.darkCard,
    alignItems: 'center',
    borderWidth: 1.5,
    borderColor: Brand.darkCard2,
  },
  regionChipActive: {
    backgroundColor: Brand.primary,
    borderColor: Brand.primaryLight,
  },
  regionChipText: {
    color: '#888',
    fontSize: 12,
    fontWeight: '700',
  },
  regionChipTextActive: {
    color: '#FFF',
  },
});
