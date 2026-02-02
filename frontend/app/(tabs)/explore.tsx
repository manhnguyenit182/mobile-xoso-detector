import { useState } from 'react';
import { Button, Text, TextInput, View, Pressable, Modal, FlatList, StyleSheet, Alert } from 'react-native';
import { DateTimePickerAndroid } from '@react-native-community/datetimepicker';
import { checkTicketWithBackend } from '../../services/imageAnalysisService';
const PROVINCES = [
  { name: 'Hồ Chí Minh' },
  { name: 'Đồng Tháp' },
  { name: 'Cà Mau' },
  { name: 'Bến Tre' },
  { name: 'Vũng Tàu' },
  { name: 'Bạc Liêu' },
  { name: 'Đồng Nai' },
  { name: 'Cần Thơ' },
  { name: 'Sóc Trăng' },
  { name: 'Tây Ninh' },
  { name: 'An Giang' },
  { name: 'Bình Thuận' },
  { name: 'Vĩnh Long' },
  { name: 'Bình Dương' },
  { name: 'Trà Vinh' },
  { name: 'Long An' },
  { name: 'Bình Phước' },
  { name: 'Hậu Giang' },
  { name: 'Tiền Giang' },
  { name: 'Kiên Giang' },
  { name: 'Đà Lạt' },
];

export default function HomeScreen() {
  const [ticketNumber, setTicketNumber] = useState('');
  const [drawDate, setDrawDate] = useState('');
  const [provinceName, setProvinceName] = useState('');
  const [modalVisible, setModalVisible] = useState(false);

  const showDatePicker = () => {
    DateTimePickerAndroid.open({
      value: drawDate ? new Date(drawDate) : new Date(),
      onChange: (event, selectedDate) => {
        if (event.type === 'set' && selectedDate) {
          const formattedDate = selectedDate.toISOString().split('T')[0];
          setDrawDate(formattedDate);
        }
      },
      mode: 'date',
    });
  };

  const selectProvince = (name: string) => {
    setProvinceName(name);
    setModalVisible(false);
  };

  const getProvinceName = () => {
    const province = PROVINCES.find((p) => p.name === provinceName);
    return province ? `${province.name}` : 'Chọn đài xổ số';
  };

  const handleSubmit = async () => {
    console.log('Số vé:', ticketNumber);
    console.log('Ngày xổ số:', drawDate);
    console.log('Đài xổ số:', provinceName);
    try {
      const response = await checkTicketWithBackend({
        so_ve: ticketNumber,
        ngay_xo_so: drawDate,
        dai_xo_so: provinceName,
      });
      Alert.alert('Kết quả từ backend', JSON.stringify(response));
    } catch (error) {
      console.error('Error submitting ticket data:', error);
      Alert.alert('Lỗi', 'Không thể gửi dữ liệu vé số lên backend');
    }
  };
  return (
    <View>
      <Text>Home Screen</Text>
      <Text>Số vé:</Text>{' '}
      <TextInput
        style={{ borderWidth: 1, marginBottom: 10, padding: 8 }}
        placeholder="Nhập số vé"
        value={ticketNumber}
        keyboardType="numeric"
        maxLength={6}
        onChangeText={setTicketNumber}
      />{' '}
      <Text>Ngày xổ số:</Text>{' '}
      <Pressable
        style={{ borderWidth: 1, marginBottom: 10, padding: 8, backgroundColor: '#f0f0f0' }}
        onPress={showDatePicker}
      >
        <Text>{drawDate || 'Chọn ngày (YYYY-MM-DD)'}</Text>
      </Pressable>{' '}
      <Text>Đài xổ số:</Text>{' '}
      <Pressable
        style={{ borderWidth: 1, marginBottom: 10, padding: 8, backgroundColor: '#f0f0f0' }}
        onPress={() => setModalVisible(true)}
      >
        <Text>{getProvinceName()}</Text>
      </Pressable>{' '}
      <Button title="Gửi dữ liệu" onPress={handleSubmit} />
      <Modal
        visible={modalVisible}
        transparent={true}
        animationType="slide"
        onRequestClose={() => setModalVisible(false)}
      >
        <View style={styles.modalContainer}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Chọn đài xổ số</Text>
            <FlatList
              data={PROVINCES}
              keyExtractor={(item) => item.name}
              renderItem={({ item }) => (
                <Pressable style={styles.provinceItem} onPress={() => selectProvince(item.name)}>
                  <Text>{item.name}</Text>
                </Pressable>
              )}
            />
            <Button title="Đóng" onPress={() => setModalVisible(false)} />
          </View>
        </View>
      </Modal>
    </View>
  );
}
const styles = StyleSheet.create({
  modalContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  modalContent: {
    backgroundColor: 'white',
    borderRadius: 10,
    padding: 20,
    width: '80%',
    maxHeight: '70%',
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 15,
    textAlign: 'center',
  },
  provinceItem: {
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
});
