const bloodGroups = [ { value: "A_POSITIVE", label: "A+" }, { value: "A_NEGATIVE", label: "A-" }, { value: "B_POSITIVE", label: "B+" }, { value: "B_NEGATIVE", label: "B-" }, { value: "AB_POSITIVE", label: "AB+" }, { value: "AB_NEGATIVE", label: "AB-" }, { value: "O_POSITIVE", label: "O+" }, { value: "O_NEGATIVE", label: "O-" }, ];

const bloodGroup: Record<string, string> = {
  A_POSITIVE: "A+",
  A_NEGATIVE: "A-",
  B_POSITIVE: "B+",
  B_NEGATIVE: "B-",
  AB_POSITIVE: "AB+",
  AB_NEGATIVE: "AB-",
  O_POSITIVE: "O+",
  O_NEGATIVE: "O-",
};

const doctorSpecializations =[
  "Cardiology",
  "Neurology",
  "Orthopedics",
  "Pediatrics",
  "Dermatology",
  "General Surgery",
  "Psychiatry",
  "Ophthalmology",
  "ENT",
  "Gynecology",
  "Oncology",
  "Urology",
  "Nephrology",
  "Gastroenterology",
  "Endocrinology",
  "Pulmonology",
  "Rheumatology",
  "Anesthesiology",
  "Pathology",
  "Radiology"
]


const doctorDepartments = [
  "Cardiology",
  "Neurology",
  "Orthopedics",
  "Pediatrics",
  "Dermatology",
  "General Surgery",
  "Psychiatry",
  "Ophthalmology",
  "ENT",
  "Gynecology",
  "Oncology",
  "Urology",
  "Nephrology",
  "Gastroenterology",
  "Endocrinology",
  "Pulmonology",
  "Rheumatology",
  "Anesthesiology",
  "Pathology",
  "Radiology",
  "Hematology",
  "Immunology",
  "Infectious Diseases",
  "Plastic Surgery",
  "Vascular Surgery",
  "Orthodontics",
  "Sports Medicine",
  "Critical Care",
  "Emergency Medicine"
]

export {bloodGroups,doctorSpecializations,doctorDepartments,bloodGroup}
