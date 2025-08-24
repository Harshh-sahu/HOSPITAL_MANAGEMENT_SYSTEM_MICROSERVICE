

const arrayToCSV = (arr: string[]) => {
    if(!arr || arr.length === 0) return "";
    return arr.join(", ");

}

 const parseToArray = (value: any) => {
  if (!value) return [];
  if (Array.isArray(value)) return value;
  try {
    const parsed = JSON.parse(value);
    return Array.isArray(parsed) ? parsed : [String(parsed)];
  } catch (e) {
    return [String(value)];
  }
};

export { arrayToCSV ,parseToArray};