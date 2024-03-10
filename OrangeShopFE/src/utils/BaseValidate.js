import { ext, regex, required, mimes, image, size, email, required_if, integer, double } from 'vee-validate/dist/rules'
import { extend } from 'vee-validate'

extend('required', {
  ...required,
  message: ' bắt buộc nhập'
})
extend('required_if', {
  ...required_if,
  message: ' bắt buộc nhập'
})
extend('integer', {
  ...integer,
  message: ' phải là số nguyên'
})
extend('double', {
  ...double,
  message: ' phải là số'
})
extend('minValue', {
  validate(value, { minValue }) {
    return parseInt(value) >= parseInt(minValue)
  },
  params: ['minValue'],
  message: ` phải lớn hơn {minValue}`
})
extend('maxValue', {
  validate(value, { maxValue }) {
    return parseInt(value) <= parseInt(maxValue)
  },
  params: ['maxValue'],
  message: ` phải nhỏ hơn {maxValue}`
})
extend('password', {
  params: ['target'],
  validate(value, { target }) {
    return value === target
  },
  message: ' không khớp với mật khẩu xác nhận'
})
extend('requiredSelect', {
  ...required,
  message: ' bắt buộc chọn'
})
extend('ext', {
  ...ext,
  message: ' không đúng định dạng'
})
extend('regex', {
  ...regex,
  message: ' không đúng định dạng'
})
extend('phone', {
  validate: value => {
    const regexPattern = /^0(3\d{8}|5\d{8}|7\d{8}|8\d{8}|9\d{8})$/
    return regexPattern.test(value)
  },
  message: ' không đúng định dạng'
})
extend('regexPass', {
  validate: value => {
    const regexPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()\-_=+\\\|\[\]{};:\'",.<>\/?]).{8,}$/
    return regexPattern.test(value)
  },
  message: '  ít nhất 8 ký tự có chữ hoa, chữ thường, ký tự đặc biệt.'
})
extend('mimes', {
  ...mimes,
  message: ' không đúng định dạng'
})

extend('image', {
  ...image,
  message: ' không đúng định dạng'
})

extend('size', {
  ...size,
  message: ` phải nhỏ hơn {size}kb`
})

extend('max', {
  validate(value, args) {
    return value.toString().length <= args.length
  },
  params: ['length'],
  message: ` không vượt quá {length} ký tự`
})

extend('min', {
  validate(value, args) {
    return value.toString().length >= args.length
  },
  params: ['length'],
  message: ` không ít hơn {length} ký tự`
})

extend('isLower', {
  params: ['target'],
  validate(value, { target }) {
    return !target || +value < +target
  },
  message: ' phải nhỏ hơn chặn trên'
})

extend('email', {
  ...email,
  message: ` phải đúng định dạng`
})

extend('uniqueValues', {
  validate(value, args) {
    const { values } = args
    if (!Array.isArray(values)) return true
    const newArr = values.map(val => val.toLowerCase())
    const duplicates = newArr.filter((value, index, self) => {
      return self.indexOf(value) !== index
    })
    return !duplicates.some(val => val.toLowerCase() === value.toLowerCase())
  },
  params: ['values'],
  message: ' đã tồn tại.'
})

export default {
  data() {
    return {
      loginFields: {
        username: 'Tên đăng nhập',
        password: 'Mật khẩu'
      },
      salesScalePointFields: {
        fromSign: 'Dấu',
        fromValue: 'Chặn dưới',
        toValue: 'Chặn trên',
        toSign: 'Dấu'
      },
      equityScalePointFields: {
        fromSign: 'Dấu',
        fromValue: 'Chặn dưới',
        toValue: 'Chặn trên',
        toSign: 'Dấu'
      },
      configScalePointFields: {
        size: 'Size',
        scale: 'Quy mô'
      },
      departments: {
        branchName: 'Tên đơn vị',
        name: 'Tên phòng ban'
      },
      positions: {
        branchName: 'Tên đơn vị',
        name: 'Tên chức vụ',
        departmentName: 'Tên phòng ban'
      },
      groups: {
        name: 'Tên nhóm',
        code: 'Mã nhóm'
      },
      users: {
        groupName: 'Tên nhóm',
        username: 'Tên đăng nhập',
        name: 'Tên nhân viên'
      },
      ruleChangeRankFields: {
        content: 'Lý do'
      },
      singleScoringModelKHDNFields: {
        codeModel: 'Mã mô hình đơn',
        nameModel: 'Tên mô hình đơn',
        typeRank: 'Loại xếp hạng',
        scaleClassify: 'Phân loại quy mô',
        industryGroup: 'Nhóm ngành',
        inputModel: 'Tên mô hình nhập liệu',
        typeCode: 'Loại code',
        fileCodeModel: 'File code model',
        fileImport: 'File',
        userCreated: 'Người tạo',
        userApproved: 'Người duyệt'
      },
      configParameterPropertyFields: {
        codeProperty: 'Mã thuộc tính',
        nameProperty: 'Tên thuộc tính',
        description: 'Mô tả',
        file: 'File'
      }
    }
  },
  methods: {
    messageError(fieldName, error) {
      if (error === undefined) {
        return undefined
      }
      return fieldName + error
    }
  }
}
