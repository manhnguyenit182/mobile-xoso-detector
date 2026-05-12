import { Pressable, StyleSheet, Text } from 'react-native';
import { Brand } from '../../constants/theme';

interface DatePickerButtonProps {
  value: string;
  placeholder: string;
  icon?: string;
  onPress: () => void;
}

export function DatePickerButton({ value, placeholder, icon = '📅', onPress }: DatePickerButtonProps) {
  return (
    <Pressable style={styles.datePicker} onPress={onPress}>
      <Text style={[styles.datePickerText, !value && styles.placeholder]}>
        {value || placeholder}
      </Text>
      <Text style={styles.datePickerIcon}>{icon}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  datePicker: {
    backgroundColor: Brand.darkCard,
    borderRadius: 12,
    borderWidth: 1.5,
    borderColor: Brand.darkCard2,
    padding: 16,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  datePickerText: {
    color: '#FFF',
    fontSize: 15,
    fontWeight: '600',
  },
  placeholder: {
    color: '#555',
    fontWeight: '400',
  },
  datePickerIcon: {
    fontSize: 20,
  },
});
